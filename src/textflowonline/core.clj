(ns textflowonline.core
  (:require
   [clojure.contrib.str-utils :as str-u]
   [appengine-magic.core :as ae]
   [appengine-magic.services.user :as ae-user])
  (:use
   textflowonline.textflow
   textflowonline.mail
   compojure.core
   (sandbar stateful-session)
   hiccup.core
   hiccup.page-helpers
   hiccup.form-helpers
   ring.util.response))

;;; generic utils
(defn html-new-line [str]
  (str-u/re-gsub #"\n" "<br>" str))

(defn submiter [value]
  [:input {:type "submit" :value value :name "submit"}])

(defn nick []
  (.getNickname (ae-user/current-user)))

;;; conctants
(def *css* [:link {:type "text/css" :rel "stylesheet" :href "stylesheets/main.css"}])

(def *about*
     (html
      [:p] "tip: make sure to paste with Courier New or Consolas font, to keep spacing correct"
      [:hr]
      [:p"This is a toy service which come with NO warranties what so ever."]
      [:p"(str \"tzach.\"  \"livyatan\" \"@gmail.com\")"]
      [:p "v.1l"]
      [:p][:img {:src "http://code.google.com/appengine/images/appengine-noborder-120x30.gif" :alt "Powered by Google App Engine"}]
      ))

(def *example*
     (str
      "[[hi Tzach Mori]\n"
      "[hello Mori Shay]\n"
      "[\"New version?\" Shay Tzach]\n"
      "[]\n"
      "[\"Yes. 1l\" Tzach Shay]]"))

;;; mail related funcs

;(def *mail-from* "admin@textflowonline.appspot.com")
(def *mail-from* "admin@textflowonline.appspotmail.com")
(def *mail-bcc* "tzach.livyatan@gmail.com")

(defn mail-sub [in out]
  (str "your text call-flow on "
       (print-str (vec (extract-actors (rec-to-strs (read-string in)))))
       " has arrive"))

(defn mail-body [user in out]
  (str "Hi " user "!\n"
       "Thanks you for using Text Flow Online\n"
       "-------------------------------------------------------\n\n\n"
       "Here is the call flow you created:\n" in "\n\n"
       out
       "\n\n\n"
       "Good day from the http://textflowonline.appspot.com/ team\n\n"
       "please do not reply this mail"))

(defn smail [input output]
  (send-mail
   *mail-from*
   (.getEmail (ae-user/current-user))
   (mail-sub input output)
   (mail-body (nick) input output)
   :bcc *mail-bcc*
   ))

;;; HTML form related funcs
(defn textarea-output [tx type]
  (html
   [type {:name "output"
	       :cols (str (max 60 (+ 5 (count (re-find #".*\n" tx)))))
	       :rows "15"
	       :class "outtext"} tx]))

(defn textarea-input [tx type]
  (html [type {:name "input" :cols "30" :rows "15" :class "intext"} tx]))



(defn write-or-err [req]
  "parse request, return error if fail"
  (try
    (write-flow (rec-to-strs (read-string req)))
    (catch Exception e (str "Fail to generate flow\n" e))))

(defn main-form []
  "generate the main HTML frontend"
  (let [req (session-get :req *example*)
	login? (ae-user/user-logged-in?)
	status (session-get :status false)]
    (html
     (doctype :html4)
     [:head *css*
      [:h1 "Online generation of RFC like call flows"]
      [:h3 "a.k.a sequence diagrams"]
      ]
     [:body
      (form-to {:name "form"} [:post "/"]
	       (textarea-input req :textarea)
	       [:p] (submiter "generate")
	       [:p] (textarea-output (write-or-err req) :textarea)
	       [:p (if login?
		     (if (= status :sent)
		       [:p "mail sent to "(nick)]
		       [:p "Hi " (nick) ", "
			(submiter "mail")
			" yourself the call flow, or " (submiter "logout")])
		     [:p "want to mail your self the call flow? "
		      (submiter "login")])])
      *about*
      ])))


(defroutes textflowonline-app-handler
  (GET "/" [] (main-form))
  (POST "/"  {params :params}
	(let [input (get params "input")
	      output (get params "output")]
	  (do
	    (session-put! :req input)
	    (session-put! :status :false)
	    (condp = (get params "submit")
		"login" (redirect (ae-user/login-url))
		"logout" (redirect (ae-user/logout-url))
		"mail"(do
			(smail input output)
			(session-put! :status :sent)
			(main-form))
		"generate" (main-form)))))
  (GET "/*" [] (html [:h3 "So sorry, page not found. try the" (link-to "/" "main") "page"])))


(def app (-> textflowonline-app-handler
             wrap-stateful-session))

(ae/def-appengine-app textflowonline-app #'app)
;;(ae/start textflowonline-app)

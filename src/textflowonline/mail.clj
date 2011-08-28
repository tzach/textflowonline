(ns textflowonline.mail
  (:import [java.util Properties]
	   [javax.mail Message MessagingException Session Transport]
	   [javax.mail.internet AddressException InternetAddress MimeMessage]))


(defn send-mail [from to subject text & {:keys [cc bcc]}]
  (Transport/send
   (let [msg  (MimeMessage. (Session/getDefaultInstance (Properties.)))]
     (doto msg
       (.setFrom (InternetAddress. from))
       (.addRecipient (javax.mail.Message$RecipientType/TO) (InternetAddress. to))
       (.setSubject subject)
       (.setText text "UTF-8"))
     (when cc (.addRecipient msg (javax.mail.Message$RecipientType/CC) (InternetAddress. cc)))
     (when bcc (.addRecipient msg (javax.mail.Message$RecipientType/BCC) (InternetAddress. bcc)))
     msg)))

;(defn foo [a & {:keys [b c]}]
;  [a b c])
       
     
;(send "test@tzach.org" "tzach.livyatan@gmail.com" "test1" "test1!!!")
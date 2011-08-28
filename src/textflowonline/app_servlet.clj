(ns textflowonline.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use textflowonline.core)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))


(defn -service [this request response]
  ((make-servlet-service-method textflowonline-app) this request response))

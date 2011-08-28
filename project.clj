(defproject textflowonline "1.0.0-SNAPSHOT"
  :description "online generation of text call flows"
  :namespaces [textflowonline.app_servlet]
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [compojure "0.5.1"]
		 [hiccup "0.3.0"]
		 [sandbar/sandbar "0.3.0-SNAPSHOT"]]
  :dev-dependencies [[appengine-magic "0.3.0-SNAPSHOT"]
		     [swank-clojure "1.2.1"]]
  )

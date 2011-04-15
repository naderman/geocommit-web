					; geocommit.com HTTP main entry point
					; (c) 2010 David Soria Parra <dsp+geocommit@experimentalworks.net>
					;          Nils Adermann <naderman+geocommit@naderman.de>
					;          Filip Noetzel <filip+geocommit@j03.de>
					; Licensed under the terms of the MIT License
(ns #^{:doc "HTTP API entry point. This provides the main entry point for all routes to the
 services and dispatches the handler functions.",
     :author "David Soria Parra"}
  geocommit.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use compojure.core
	[compojure.route :as route]
	[compojure.handler :as handler]
	[geocommit.hook :only [app-hook]]
	[geocommit.api :only [app-api-geocommits]]
	[geocommit.signup :only [app-verify-hook app-signup]]
	[geocommit.http :only [mslurp]])
  (:require [compojure.route :as route]
	    [clojure.contrib.trace :as t]))

(defroutes main-routes
  (GET "/api/geocommits"
       [payload]
       (app-api-geocommits payload))
  (POST "/api*"
	payload
	(if (= (:content-type payload) "application/githubpostreceive+json")
	  (app-hook (mslurp (.getInputStream (:request payload))))
	  (app-hook (:payload (:params payload)))))
  (GET "/signup/verify/:code"
       [code]
       (app-verify-hook code))
  (POST "/signup*"
	[mailaddr]
	(app-signup mailaddr))
  (route/not-found "not a valid request"))

(def app
     (handler/site main-routes))

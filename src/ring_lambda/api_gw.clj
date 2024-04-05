(ns ring-lambda.api-gw
  (:require [clojure.string :as s]
            [ring.util.codec :as codec])
  (:import [clojure.lang ISeq]
           [java.io File InputStream]))

(defn ->ring-request
  "Transform lambda input to Ring requests. Has two extra properties:
   :event - the lambda input
   :context - an instance of a lambda context
              http://docs.aws.amazon.com/lambda/latest/dg/java-context-object.html"
  [event context]
  (let [[http-version host]
        (s/split (get-in event [:headers :Via] "") #" ")]
    {:server-port
     (try
       (Integer/parseInt (get-in event [:headers :X-Forwarded-Port]))
       (catch NumberFormatException _e nil))
     :body           (get event :body)
     :server-name    host
     :remote-addr    (get event :sourceIp "")
     :uri            (get event :path)
     :query-string   (codec/form-encode (get event :queryStringParameters {}))
     :scheme         (keyword
                      (get-in event [:headers :X-Forwarded-Proto]))
     :request-method (keyword
                      (s/lower-case (get event :httpMethod "")))
     :protocol       (format "HTTP/%s" http-version)
     :headers        (into {} (map (fn -header-keys [[k v]]
                                     [(s/lower-case (name k)) v])
                                   (:headers event)))
     :event          event
     :context        context}))

(defmulti wrap-body class)
(defmethod wrap-body String [body] body)
(defmethod wrap-body ISeq [body] (s/join body))
(defmethod wrap-body File [body] (slurp body))
(defmethod wrap-body InputStream [body] (slurp body))

(defn ->api-gw-response [response]
  {:statusCode      (:status response)
   :body            (wrap-body (:body response))
   :headers         (:headers response)})

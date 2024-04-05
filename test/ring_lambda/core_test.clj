(ns ring-lambda.core-test
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]
            [ring-lambda.core :refer [-handleRequest]])
  (:import [java.io ByteArrayOutputStream]))

(deftest api-gw-handler-test
  (let [event (io/input-stream (io/resource "sample-apigw-event.json"))
        out (ByteArrayOutputStream.)
        _ (-handleRequest nil event out nil)
        {:keys [statusCode body headers]} (-> (.toString out) (json/parse-string true))]
    (is (= 200 statusCode))
    (is (= {:total 3} (json/parse-string body true)))
    (is (= "application/json; charset=utf-8" (get headers :Content-Type)))))
(ns ritz.swank.indent-test
  (:use clojure.test)
  (:require
   [ritz.swank.indent :as indent]))

(deftest update-indentation-delta-test
  (is (nil? (#'indent/update-indentation-delta
             (the-ns 'ritz.swank.indent-test)
             (ref {})
             false)))
  (is (every?
       identity
       ((juxt seq #(every? (fn [x]
                             (and
                              (string? (first x))
                              (or (integer? (second x))
                                  (= 'defun (second x)))
                              (sequential? (last x)))) %))
        (#'indent/update-indentation-delta
         (the-ns 'ritz.swank.indent-test)
         (ref {})
         true)))))

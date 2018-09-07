(ns calculator.basic-calc-test
  (:require
    [calculator.events :as events]
    [calculator.subs :as subs]
    [cljs.test :refer-macros [deftest is testing run-tests use-fixtures]]
    [re-frame.core :as rf]
    [re-frame.db :as rf.db]))

(defn clear-db [] (reset! rf.db/app-db events/default-db))

(use-fixtures :each {:before clear-db})

(defn dispatch-events [& events]
  (doseq [event events]
    (rf/dispatch-sync
      (cond
        (= \% event) [::events/percent]
        (= \. event) [::events/decimal]
        (= (type event) (type 1)) [::events/number event]
        (= event =) [::events/equal]
        (= (type event) (type +)) [::events/operation event]
        (keyword? event) [event]))))

(deftest add
  (let [display (rf/subscribe [::subs/display])]
    (dispatch-events 2 + 3 =)
    (is (= "5" @display))
    (dispatch-events + 3 2)
    (is (= "32" @display))
    (dispatch-events +)
    (is (= "37" @display))
    (dispatch-events + 1 0)
    (is (= "10" @display))))

(deftest subtract
  (let [display (rf/subscribe [::subs/display])]
    (dispatch-events 2 - 3 =)
    (is (= "-1" @display))
    (dispatch-events - 2 1 -)
    (is (= "-22" @display))
    (clear-db)
    (dispatch-events - 3)
    (is (= "3" @display))
    (dispatch-events =)
    (is (= "-3" @display))))

(deftest negate
  (let [display (rf/subscribe [::subs/display])]
    (dispatch-events 2 0)
    (is (= "20" @display))
    (dispatch-events ::events/negate)
    (is (= "-20" @display))))

(deftest decimal
  (let [display (rf/subscribe [::subs/display])]
    (dispatch-events 2 \. \. \. 0)
    (is (= "2.0" @display))
    (dispatch-events 5 2)
    (is (= "2.052" @display))))

(deftest percent
  (let [display (rf/subscribe [::subs/display])]
    (dispatch-events 2 0 \%)
    (is (= "0.2" @display))
    (dispatch-events 2 1 \%)
    (is (= "0.21" @display))
    (dispatch-events 8 2 0 0 * 7 0 \%)
    (is (= "5740" @display))
    (dispatch-events 1 2 \%)
    (is (= "0.12" @display))))

(deftest equals
  (let [display (rf/subscribe [::subs/display])]
    (dispatch-events 1 \. 0 0 5 =)
    (is (= "1.005" @display))
    (dispatch-events 1 2 * 2 =)
    (is (= "24" @display))
    (dispatch-events =)
    (is (= "48" @display))
    (dispatch-events - =)
    (is (= "0" @display))
    (dispatch-events =)
    (is (= "-48" @display))
    ;todo not sure how to format correctly
    ;(dispatch-events =)
    ;(is (= "1.01" @display))
    ))

(comment
  (cljs.test/run-tests))

(ns calculator.subs
  (:require
    [re-frame.core :as rf]))

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

(rf/reg-sub
  ::display
  (fn [{:keys [current-num]}]
    (str current-num)))

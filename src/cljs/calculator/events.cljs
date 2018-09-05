(ns calculator.events
  (:require
    [clojure.spec.alpha :as s]
    [re-frame.core :as rf]))

#_(s/def db
    (s/keys :req-un [:state :operation :previous-num :current-num]))
(s/def ::current-num integer?)
(s/def ::previous-num (s/or :none nil? :num integer?))
(s/def ::state integer? #_(s/or :ready :operation :entered-num :decimal :equals))

(defn parse-num [num]
  (js/parseFloat num))

(defn format-num [num]
  num
  ;todo How to properly format
  ;(js/Number. (str (.round js/Math (str num "e2")) "e-2"))
  )

;spec would be nice to list valid states, operations
(def default-db
  {:state :equals
   :operation nil
   :previous-num nil
   :current-num 0})

;todo use clojure.spec

(rf/reg-event-db
  ::initialize-db
  (fn [_ _]
    default-db))

(rf/reg-event-db
  ::number
  [rf/debug]
  (fn [{:keys [state] :as db} [_ num]]
    (condp = state
      :entered-num
      (update db :current-num #(str % num))

      (assoc db :current-num num
                :state :entered-num))))

(rf/reg-event-db
  ::clear-display
  (fn [db _]
    default-db))

(rf/reg-event-db
  ::delete
  (fn [db _]
    (update db :current-num #(quot % 10))))

(rf/reg-event-db
  ::negate
  [rf/debug]
  (fn [db _]
    (update db :current-num #(* -1 %))))

(rf/reg-event-db
  ::decimal
  [rf/debug]
  (fn [{:keys [current-num] :as db} _]
    (if (.includes (str current-num) ".")
      db
      (update db :current-num #(str % ".")))))

(rf/reg-event-db
  ::percent
  [rf/debug]
  (fn [{:keys [current-num previous-num operation]} _]
    (let [percent-op #(-> % parse-num (/ 100))]
      {:current-num (if previous-num
                      (operation (parse-num previous-num)
                                 (percent-op current-num))
                      (percent-op current-num))
       :state :percent})))

(rf/reg-event-db
  ::operation
  [rf/debug]
  (fn [{:keys [current-num previous-num state operation] :as db} [_ op-fn]]
    (cond
      (or (= state :equals) (nil? previous-num))
      (assoc db :operation op-fn
                :state :operation
                :previous-num current-num)

      (= state :operation)
      (assoc db :operation op-fn)

      (= state :entered-num)
      (let [new-num (operation (parse-num previous-num) (parse-num current-num))]
        (assoc db :operation op-fn
                  :state :operation
                  :current-num new-num
                  :previous-num new-num))

      :else
      db)))

(rf/reg-event-db
  ::equal
  [rf/debug]
  (fn [{:keys [current-num previous-num operation state] :as db} _]
    (cond
      (or (and (= :entered-num state) operation) (= :operation state))
      (letfn [(working-op-fn [x] (format-num (operation (parse-num x) (parse-num current-num))))]
        {:current-num (working-op-fn (or previous-num current-num))
         :working-op-fn working-op-fn
         :state :equals})

      (= :equals state)
      (let [{:keys [working-op-fn]} db]
        (assoc db :current-num (working-op-fn current-num)))

      :else
      (-> db
          (assoc :state :equals)
          (update :current-num #(-> % parse-num format-num))))))

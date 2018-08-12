(ns calculator.events
  (:require
   [re-frame.core :as re-frame]
   [calculator.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
  ::update-display
  (fn [db [_ num]]
    (if (= :entering-nums (:state db))
      (update db :display #(-> % (* 10) (+ num)))
      (assoc db :display num :state :entering-nums))))

(re-frame/reg-event-db
  ::clear-display
  (fn [db _]
    db/default-db))

(re-frame/reg-event-db
  ::operation
  (fn [db [_ op-fn]]
    (cond-> (assoc db :state :operation)

            (and (:working-num db) (:op-fn db))
            (-> (update :display #(op-fn (:working-num db) %))
                (update :working-num #(op-fn % (:display db))))

            (and (not (:working-num db)) (= :entering-nums (:state db)))
            (assoc :working-num (:display db)
                   :op-fn op-fn)

            (and (:working-num db) (= :entering-nums (:state db)))
            (-> (update :display #(op-fn (:working-num db) %))
                (update :working-num #(op-fn % (:display db)))))))

(re-frame/reg-event-db
  ::equal
  (fn [db _]
    (if (:op-fn db)
      (-> db
          (assoc :state :equals :working-num nil)
          (update :display #((:op-fn db) (:working-num db) %)))
      (-> db
          (assoc :state :equals :working-num nil)
          (update :display #(+ % (:working-num db)))))))

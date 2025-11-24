CREATE DATABASE IF NOT EXISTS events;
USE events;

-- Kafka 메시지 원천 테이블 (Kafka Engine)
CREATE TABLE IF NOT EXISTS kafka_events
(
    `timestamp` Nullable(String),
    `event_type` Nullable(String),

    `ip_address` Nullable(String),

    `user_id` Nullable(String),
    `value` Nullable(String),
    `attrs` Nullable(String),

    -- 위치정보 필드
    `country_code` Nullable(String),
    `country_name` Nullable(String),
    `city_name` Nullable(String),
    `latitude` Nullable(Float64),
    `longitude` Nullable(Float64),
    `timezone` Nullable(String)
)
    ENGINE = Kafka
    SETTINGS
      kafka_broker_list = 'kafka:9092',
      kafka_topic_list = 'events_json',
      kafka_group_name = 'ch-consumer-group',
      kafka_format = 'JSONEachRow',
      kafka_num_consumers = 1,
      kafka_handle_error_mode = 'stream';

-- 적재 대상 테이블 (MergeTree)
CREATE TABLE IF NOT EXISTS fact_events
(
    `event_time` DateTime DEFAULT now(),
    `event_date` Date DEFAULT toDate(event_time),
    `event_type` LowCardinality(String),
    `ip_address` String,
    `user_id`    String,
    `value`      String,
    `attrs`      String,

    -- 위치정보 필드
    `country_code` LowCardinality(String),
    `country_name` LowCardinality(String),
    `city_name` String,
    `latitude` Float64,
    `longitude` Float64,
    `timezone` LowCardinality(String)
)
    ENGINE = MergeTree
    PARTITION BY toYYYYMM(event_date)
    ORDER BY (event_date, country_code, event_type, user_id);

-- Materialized View: Kafka → fact_events
DROP VIEW IF EXISTS mv_kafka_to_fact;
CREATE MATERIALIZED VIEW mv_kafka_to_fact
TO fact_events AS
SELECT
    ifNull(parseDateTimeBestEffortOrNull(timestamp), now()) AS event_time,
    ifNull(event_type, '') AS event_type,
    ifNull(ip_address, '') AS ip_address,
    ifNull(user_id, '') AS user_id,
    ifNull(value, '') AS value,
    ifNull(attrs, '{}') AS attrs,
    ifNull(country_code, '') AS country_code,
    ifNull(country_name, '') AS country_name,
    ifNull(city_name, '') AS city_name,
    ifNull(latitude, 0.0) AS latitude,
    ifNull(longitude, 0.0) AS longitude,
    ifNull(timezone, '') AS timezone
FROM kafka_events;

-- 기본 지표용 뷰 (집계)
CREATE VIEW IF NOT EXISTS v_event_minute_agg AS
SELECT
    toStartOfMinute(event_time) AS minute,
    event_type,
    count() AS cnt,
    uniqExact(user_id) AS uu
FROM fact_events
GROUP BY minute, event_type
ORDER BY minute DESC;
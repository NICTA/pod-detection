# encoding: utf-8
require "beefcake"

# Zenoss Protocol Buffers generated by beefcake then cleaned up and
# consolidated by hand.
#
module Org
  module Zenoss
    module Protobufs

      module Util

        class TimestampRange
          include Beefcake::Message

          optional :start_time, :uint64, 1
          optional :end_time, :uint64, 2
        end

        class ScheduleWindow
          include Beefcake::Message

          module RepeatType
            NEVER = 0
            DAILY = 1
            EVERY_WEEKDAY = 2
            WEEKLY = 3
            MONTHLY = 4
            FIRST_SUNDAY = 5
          end

          optional :uuid, :string, 1
          optional :name, :string, 2
          optional :enabled, :bool, 3
          optional :created_time, :uint64, 4
          optional :duration_seconds, :int32, 5
          optional :repeat, ScheduleWindow::RepeatType, 6
        end

        class ScheduleWindowSet
          include Beefcake::Message

          repeated :windows, ScheduleWindow, 1
        end

        class Property
          include Beefcake::Message

          required :name, :string, 1
          required :value, :string, 2
        end

      end # module Org::Zenoss::Protobufs::Util

      module Model

        module ModelElementType
          DEVICE = 1
          COMPONENT = 2
          SERVICE = 3
          ORGANIZER = 4
        end

        class Device
          include Beefcake::Message

          optional :uuid, :string, 1
          optional :id, :string, 2
          optional :title, :string, 3
        end

        class Component
          include Beefcake::Message

          optional :uuid, :string, 1
          optional :id, :string, 2
          optional :title, :string, 3
          optional :device, Device, 4
        end

        class Organizer
          include Beefcake::Message

          optional :uuid, :string, 1
          optional :title, :string, 2
          optional :path, :string, 3
        end

        class Service
          include Beefcake::Message

          optional :uuid, :string, 1
          optional :title, :string, 2
          repeated :impacts, :string, 3
        end

      end # module Org::Zenoss::Protobufs::Util

      module Zep

        module EventSeverity
          SEVERITY_CLEAR = 0
          SEVERITY_DEBUG = 1
          SEVERITY_INFO = 2
          SEVERITY_WARNING = 3
          SEVERITY_ERROR = 4
          SEVERITY_CRITICAL = 5
        end

        module SyslogPriority
          SYSLOG_PRIORITY_EMERG = 0
          SYSLOG_PRIORITY_ALERT = 1
          SYSLOG_PRIORITY_CRIT = 2
          SYSLOG_PRIORITY_ERR = 3
          SYSLOG_PRIORITY_WARNING= 4
          SYSLOG_PRIORITY_NOTICE = 5
          SYSLOG_PRIORITY_INFO = 6
          SYSLOG_PRIORITY_DEBUG = 7
        end

        module EventStatus
          STATUS_NEW = 0
          STATUS_ACKNOWLEDGED = 1
          STATUS_SUPPRESSED = 2
          STATUS_CLOSED = 3
          STATUS_CLEARED = 4
          STATUS_DROPPED = 5
          STATUS_AGED = 6
        end

        module FilterOperator
          OR = 1
          AND = 2
        end

        module RuleType
          RULE_TYPE_JYTHON = 1
        end

        class EventActor
          include Beefcake::Message

          optional :element_type_id, Org::Zenoss::Protobufs::Model::ModelElementType, 1
          optional :element_uuid, :string, 2
          optional :element_identifier, :string, 3
          optional :element_title, :string, 4
          optional :element_sub_type_id, Org::Zenoss::Protobufs::Model::ModelElementType, 5
          optional :element_sub_uuid, :string, 6
          optional :element_sub_identifier, :string, 7
          optional :element_sub_title, :string, 8
        end

        class EventDetail
          include Beefcake::Message

          module EventDetailMergeBehavior
            REPLACE = 1
            APPEND = 2
            UNIQUE = 3
          end

          required :name, :string, 1
          repeated :value, :string, 2
          optional :merge_behavior, EventDetail::EventDetailMergeBehavior, 3, :default => EventDetail::EventDetailMergeBehavior::REPLACE
        end

        class EventDetailSet
          include Beefcake::Message

          repeated :details, EventDetail, 1
        end

        class EventTag
          include Beefcake::Message

          required :type, :string, 1
          repeated :uuid, :string, 2
        end

        class EventNote
          include Beefcake::Message

          optional :uuid, :string, 1
          optional :user_uuid, :string, 2
          optional :user_name, :string, 3
          optional :created_time, :uint64, 4
          required :message, :string, 5
        end

        class Event
          include Beefcake::Message

          optional :uuid, :string, 1
          optional :created_time, :uint64, 2
          optional :fingerprint, :string, 3
          optional :event_class, :string, 4
          optional :event_class_key, :string, 5
          optional :event_class_mapping_uuid, :string, 6
          optional :actor, EventActor, 7
          optional :summary, :string, 8
          optional :message, :string, 9
          optional :severity, EventSeverity, 10, :default => EventSeverity::SEVERITY_INFO
          optional :event_key, :string, 12
          optional :event_group, :string, 13
          optional :agent, :string, 14
          optional :syslog_priority, SyslogPriority, 15
          optional :syslog_facility, :uint32, 16
          optional :nt_event_code, :uint32, 17
          optional :monitor, :string, 18
          repeated :details, EventDetail, 19
          repeated :tags, EventTag, 20
          optional :summary_uuid, :string, 21
          optional :status, EventStatus, 22, :default => EventStatus::STATUS_NEW
          optional :apply_transforms, :bool, 23, :default => true
          optional :count, :uint32, 24, :default => 1
          optional :first_seen_time, :uint64, 25
        end

        class ZepRawEvent
          include Beefcake::Message

          required :event, Event, 1
          repeated :clear_event_class, :string, 2
        end

        class EventAuditLog
          include Beefcake::Message

          required :timestamp, :uint64, 1
          required :new_status, EventStatus, 2
          optional :user_uuid, :string, 3
          optional :user_name, :string, 4
        end

        class EventSummary
          include Beefcake::Message

          optional :uuid, :string, 1
          repeated :occurrence, Event, 2
          optional :status, EventStatus, 3, :default => EventStatus::STATUS_NEW
          optional :first_seen_time, :uint64, 4
          optional :status_change_time, :uint64, 5
          optional :last_seen_time, :uint64, 6
          optional :count, :uint32, 7, :default => 1
          optional :current_user_uuid, :string, 8
          optional :current_user_name, :string, 9
          optional :cleared_by_event_uuid, :string, 10
          repeated :notes, EventNote, 11
          repeated :audit_log, EventAuditLog, 12
          optional :update_time, :uint64, 13
        end

        class NumberRange
          include Beefcake::Message

          optional :from, :sint64, 1
          optional :to, :sint64, 2
        end

        class EventTagFilter
          include Beefcake::Message

          optional :op, FilterOperator, 1, :default => FilterOperator::OR
          repeated :tag_uuids, :string, 2
        end

        class EventDetailFilter
          include Beefcake::Message

          required :key, :string, 1
          repeated :value, :string, 2
          optional :op, FilterOperator, 3, :default => FilterOperator::OR
        end

        class EventFilter
          include Beefcake::Message

          repeated :severity, EventSeverity, 1
          repeated :status, EventStatus, 2
          repeated :event_class, :string, 3
          repeated :first_seen, :'org::zenoss::protobufs::util::TimestampRange', 4
          repeated :last_seen, :'org::zenoss::protobufs::util::TimestampRange', 5
          repeated :status_change, :'org::zenoss::protobufs::util::TimestampRange', 6
          repeated :update_time, :'org::zenoss::protobufs::util::TimestampRange', 7
          repeated :count_range, NumberRange, 8
          repeated :element_identifier, :string, 9
          repeated :element_sub_identifier, :string, 10
          repeated :uuid, :string, 11
          repeated :event_summary, :string, 12
          repeated :current_user_name, :string, 13
          repeated :tag_filter, EventTagFilter, 14
          repeated :details, EventDetailFilter, 15
          repeated :fingerprint, :string, 16
          repeated :agent, :string, 17
          repeated :monitor, :string, 18
          optional :operator, FilterOperator, 19, :default => FilterOperator::AND
          repeated :subfilter, EventFilter, 20
          repeated :element_title, :string, 21
          repeated :element_sub_title, :string, 22
          repeated :event_key, :string, 23
          repeated :event_class_key, :string, 24
          repeated :event_group, :string, 25
          repeated :message, :string, 26
        end

        class EventSort
          include Beefcake::Message

          module Field
            SEVERITY = 1
            STATUS = 2
            EVENT_CLASS = 3
            FIRST_SEEN = 4
            LAST_SEEN = 5
            STATUS_CHANGE = 6
            COUNT = 7
            ELEMENT_IDENTIFIER = 8
            ELEMENT_SUB_IDENTIFIER = 9
            EVENT_SUMMARY = 10
            UPDATE_TIME = 11
            CURRENT_USER_NAME = 12
            AGENT = 13
            MONITOR = 14
            UUID = 15
            FINGERPRINT = 16
            DETAIL = 17
            ELEMENT_TITLE = 18
            ELEMENT_SUB_TITLE = 19
            EVENT_KEY = 20
            EVENT_CLASS_KEY = 21
            EVENT_GROUP = 22
          end

          module Direction
            ASCENDING = 1
            DESCENDING = 2
          end

          required :field, EventSort::Field, 1
          optional :direction, EventSort::Direction, 2, :default => EventSort::Direction::ASCENDING
          optional :detail_key, :string, 3
        end

        class EventSummaryRequest
          include Beefcake::Message

          optional :event_filter, EventFilter, 1
          optional :exclusion_filter, EventFilter, 2
          repeated :sort, EventSort, 3
          optional :limit, :uint32, 4, :default => 1000
          optional :offset, :uint32, 5
        end

        class EventSummaryResult
          include Beefcake::Message

          repeated :events, EventSummary, 1
          optional :limit, :uint32, 2
          optional :next_offset, :uint32, 3
          optional :total, :uint32, 4
        end

        class EventSummaryUpdate
          include Beefcake::Message

          optional :status, EventStatus, 1
          optional :current_user_uuid, :string, 2
          optional :current_user_name, :string, 3
        end

        class EventQuery
          include Beefcake::Message

          optional :event_filter, EventFilter, 1
          optional :exclusion_filter, EventFilter, 2
          repeated :sort, EventSort, 3
          optional :timeout, :uint32, 4, :default => 60
        end

        class EventSummaryUpdateRequest
          include Beefcake::Message

          optional :event_query_uuid, :string, 1
          required :update_fields, EventSummaryUpdate, 2
          optional :offset, :uint32, 3, :default => 0
          optional :limit, :uint32, 4, :default => 100
        end

        class EventSummaryUpdateResponse
          include Beefcake::Message

          optional :next_request, EventSummaryUpdateRequest, 1
          optional :total, :uint32, 2
          required :updated, :uint32, 3
        end

        class EventDetailItem
          include Beefcake::Message

          module EventDetailType
            STRING = 1
            INTEGER = 2
            FLOAT = 3
            LONG = 4
            DOUBLE = 5
            IP_ADDRESS = 6
            PATH = 7
          end

          required :key, :string, 1
          optional :type, EventDetailItem::EventDetailType, 2, :default => EventDetailItem::EventDetailType::STRING
          optional :name, :string, 3
        end

        class EventDetailItemSet
          include Beefcake::Message

          repeated :details, EventDetailItem, 1
        end

        class Rule
          include Beefcake::Message

          required :api_version, :int32, 1
          required :type, RuleType, 2
          required :source, :string, 3
        end

        class EventTriggerSubscription
          include Beefcake::Message

          optional :uuid, :string, 1
          optional :delay_seconds, :int32, 2
          optional :repeat_seconds, :int32, 3
          optional :send_initial_occurrence, :bool, 4, :default => true
          required :subscriber_uuid, :string, 5
          required :trigger_uuid, :string, 6
        end

        class EventTriggerSubscriptionSet
          include Beefcake::Message

          repeated :subscriptions, EventTriggerSubscription, 1
        end

        class EventTrigger
          include Beefcake::Message

          optional :uuid, :string, 1
          optional :name, :string, 2
          optional :enabled, :bool, 3, :default => true
          required :rule, Rule, 4
          repeated :subscriptions, EventTriggerSubscription, 5
        end

        class EventTriggerSet
          include Beefcake::Message

          repeated :triggers, EventTrigger, 1
        end

        class Signal
          include Beefcake::Message

          required :uuid, :string, 1
          required :created_time, :uint64, 2
          required :trigger_uuid, :string, 3
          required :subscriber_uuid, :string, 4
          optional :clear, :bool, 5, :default => false
          optional :event, EventSummary, 6
          optional :clear_event, EventSummary, 7
          optional :message, :string, 8
        end

        class EventTagSeverity
          include Beefcake::Message

          required :severity, EventSeverity, 1
          optional :count, :uint32, 2, :default => 0
          optional :acknowledged_count, :uint32, 3, :default => 0
        end

        class EventTagSeverities
          include Beefcake::Message

          required :tag_uuid, :string, 1
          repeated :severities, EventTagSeverity, 2
          optional :total, :uint32, 3
        end

        class EventTagSeveritiesSet
          include Beefcake::Message

          repeated :severities, EventTagSeverities, 1
        end

        class ZepConfig
          include Beefcake::Message

          optional :event_age_disable_severity, EventSeverity, 1, :default => EventSeverity::SEVERITY_ERROR
          optional :event_age_interval_minutes, :uint32, 2, :default => 240
          optional :event_archive_interval_minutes, :uint32, 3, :default => 4320
          optional :event_archive_purge_interval_days, :uint32, 4, :default => 90
          optional :event_time_purge_interval_days, :uint32, 5, :default => 1
          optional :event_age_severity_inclusive, :bool, 6, :default => false
          optional :event_max_size_bytes, :uint64, 7, :default => 32768
          optional :index_summary_interval_milliseconds, :uint64, 8, :default => 1000
          optional :index_archive_interval_milliseconds, :uint64, 9, :default => 30000
          optional :index_limit, :uint32, 10, :default => 1000
          optional :aging_limit, :uint32, 11, :default => 1000
          optional :archive_limit, :uint32, 12, :default => 1000
          optional :aging_interval_milliseconds, :uint64, 13, :default => 60000
          optional :archive_interval_milliseconds, :uint64, 14, :default => 60000
        end

        class DaemonHeartbeat
          include Beefcake::Message

          required :monitor, :string, 1
          required :daemon, :string, 2
          required :timeout_seconds, :uint32, 3
          optional :last_time, :uint64, 4
        end

        class DaemonHeartbeatSet
          include Beefcake::Message

          repeated :heartbeats, DaemonHeartbeat, 1
        end

        class EventTime
          include Beefcake::Message

          optional :summary_uuid, :string, 1
          optional :processed_time, :uint64, 2
          optional :created_time, :uint64, 3
          optional :first_seen_time, :uint64, 4
        end

        class EventTimeSet
          include Beefcake::Message

          repeated :event_times, EventTime, 1
        end

        class ZepStatistic
          include Beefcake::Message

          required :name, :string, 1
          required :description, :string, 2
          required :value, :int64, 3
        end

        class ZepStatistics
          include Beefcake::Message

          repeated :stats, ZepStatistic, 1
        end

      end # module Org::Zenoss:Protobufs::Zep

    end # module Org::Zenoss::Protobufs

  end # module Org::Zenoss

end # module Org

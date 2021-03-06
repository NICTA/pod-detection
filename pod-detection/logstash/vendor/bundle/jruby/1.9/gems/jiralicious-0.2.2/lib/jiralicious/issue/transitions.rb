# encoding: utf-8
module Jiralicious
  class Issue
    ##
    # The Transitions Class provides all of the functionality to retrieve,
    # and use a transition associated with an Issue.
    #
    class Transitions < Jiralicious::Base

      # Contains the meta data to process a Transaction
      attr_accessor :meta

      ##
      # Initialization Method
      #
      def initialize(decoded_json = nil, default = nil, &blk)
        @loaded = false
        @meta = nil
        if decoded_json.is_a? Array
          if decoded_json.length == 1
            decoded_json = decoded_json[0]
          end
        end
        unless decoded_json.nil?
          if decoded_json.is_a? String
            self.class.property :jira_key
            self.jira_key = decoded_json
          elsif decoded_json.is_a? Hash
            properties_from_hash(decoded_json)
            super(decoded_json)
            parse!(decoded_json)
            @loaded = true
          else
            self.class.property :jira_key
            self.jira_key = default
            decoded_json.each do |list|
              self.class.property :"id_#{list['id']}"
              self.merge!({"id_#{list['id']}" => self.class.new(list)})
            end
          end
        end
      end

      class << self
        ##
        # Retrieves the associated Transitions based on the Issue Key
        #
        def find(key, options = {})
          response = fetch({:parent => parent_name, :parent_key => key})
          response.parsed_response['transitions'].each do |t|
            t['jira_key'] = key
          end
          a = new(response.parsed_response['transitions'], key)
          return a
        end

        ##
        # Retrieves the Transition based on the Issue Key and Transition ID
        #
        def find_by_key_and_id(key, id, options = {})
          response = fetch({:parent => parent_name, :parent_key => key, :body => {"transitionId" => id}, :body_to_params => true })
          response.parsed_response['transitions'].each do |t|
            t['jira_key'] = key
          end
          a = new(response.parsed_response['transitions'])
          return a
        end

        ##
        # Processes the Transition based on the provided options
        #
        def go(key, id, options = {})
          transition = {"transition" => {"id" => id}}
          if options[:comment].is_a? String
            transition.merge!({"update" => {"comment" => [{"add" => {"body" => options[:comment].to_s}}]}})
          elsif options[:comment].is_a? Jiralicious::Issue::Fields
            transition.merge!(options[:comment].format_for_update)
          elsif options[:comment].is_a? Hash
            transition.merge!({"update" => options[:comment]})
          end
          if options[:fields].is_a? Jiralicious::Issue::Fields
            transition.merge!(options[:fields].format_for_create)
          elsif options[:fields].is_a? Hash
            transition.merge!({"fields" => options[:fields]})
          end
          fetch({:method => :post, :parent => parent_name, :parent_key => key, :body => transition})
        end

        ##
        # Retrieves the meta data for the Transition based on the
        # options, Issue Key and Transition ID provided.
        #
        def meta(key, id, options = {})
          response = fetch({:method => :get, :parent => parent_name, :parent_key => key, :body_to_params => true,
              :body => {"transitionId" => id, "expand" => "transitions.fields"}})
          response.parsed_response['transitions'].each do |t|
            t['jira_key'] = key
          end
          a = (options[:return].nil?) ?  new(response.parsed_response['transitions'], key) : response
          return a
        end

        alias :find_all :find
      end

      ##
      # Retrieves the associated Transitions based on the Issue Key
      #
      def all
        self.class.all(self.jira_key) if self.jira_key
      end

      ##
      # Processes the Transition based on the provided options
      #
      def go(options = {})
        self.class.go(self.jira_key, self.id, options)
      end

      ##
      # Retrieves the meta data for the Transition based on the
      # options, Issue Key and Transition ID provided.
      #
      def meta
        if @meta.nil?
          l = self.class.meta(self.jira_key, self.id, {:return => true})
          @meta = Field.new(l.parsed_response['transitions'].first)
        end
        @meta
      end
    end
  end
end

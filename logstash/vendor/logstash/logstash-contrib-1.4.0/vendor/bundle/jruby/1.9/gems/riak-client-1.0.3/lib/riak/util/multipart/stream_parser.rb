require 'riak/util/translation'
require 'riak/util/multipart'

module Riak
  module Util
    module Multipart
      # This class is parses chunked/streamed multipart HTTP
      # streams. It is used by streaming MapReduce queries, and in the
      # future streaming key-lists (once implemented on the Riak side).
      class StreamParser
        include Multipart
        include Translation
        # Creates a new StreamParser.
        #
        # Example usage:
        #     http.get(200, "/riak", "foo", {}, &StreamParser.new {|part| ... })
        #
        # @yield [Hash] parts of the multipart/mixed stream,
        #               containing :headers and :body keys
        def initialize(&block)
          raise ArgumentError, t('missing_block') unless block_given?
          @buffer = ""
          @block = block
          @state = :get_boundary
        end

        # Accepts a chunk of the HTTP response stream, and yields to
        # the block when appropriate.
        def accept(chunk)
          @buffer << chunk
          @state = send(@state)
        end

        # Returns a Proc that can be passed to an HTTP request method.
        def to_proc
          method(:accept).to_proc
        end

        private
        CAPTURE_BOUNDARY = /^--([A-Za-z0-9\'()+_,-.\/:=?]+)\r?\n/

        def get_boundary
          if @buffer =~ CAPTURE_BOUNDARY
            @re = /\r?\n--#{Regexp.escape($1)}(?:--)?\r?\n/
            @buffer = $~.post_match
            buffering
          else
            :get_boundary
          end
        end

        def buffering
          while @buffer =~ @re
            @block.call parse_multipart_section($~.pre_match)
            @buffer = $~.post_match
          end
          :buffering
        end
      end
    end
  end
end

# -*- encoding: utf-8 -*-

if defined? JRUBY_VERSION
  require 'java'
  Dir["#{File.expand_path('..', __FILE__)}/*.jar"].each { |jar| $CLASSPATH << jar }
else
  abort "geotools-jars requires JRuby (http://jruby.org)"
end


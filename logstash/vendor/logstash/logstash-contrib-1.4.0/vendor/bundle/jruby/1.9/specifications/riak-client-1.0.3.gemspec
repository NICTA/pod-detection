# -*- encoding: utf-8 -*-
# stub: riak-client 1.0.3 ruby lib

Gem::Specification.new do |s|
  s.name = "riak-client"
  s.version = "1.0.3"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Sean Cribbs"]
  s.date = "2012-04-17"
  s.description = "riak-client is a rich client for Riak, the distributed database by Basho. It supports the full HTTP and Protocol Buffers interfaces including storage operations, bucket configuration, link-walking, secondary indexes and map-reduce."
  s.email = ["sean@basho.com"]
  s.homepage = "http://github.com/basho/riak-ruby-client"
  s.require_paths = ["lib"]
  s.rubygems_version = "2.1.9"
  s.summary = "riak-client is a rich client for Riak, the distributed database by Basho."

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<rspec>, ["~> 2.8.0"])
      s.add_development_dependency(%q<fakeweb>, [">= 1.2"])
      s.add_development_dependency(%q<rack>, [">= 1.0"])
      s.add_development_dependency(%q<excon>, [">= 0.6.1"])
      s.add_development_dependency(%q<rake>, [">= 0"])
      s.add_runtime_dependency(%q<i18n>, [">= 0.4.0"])
      s.add_runtime_dependency(%q<builder>, [">= 2.1.2"])
      s.add_runtime_dependency(%q<beefcake>, ["~> 0.3.7"])
      s.add_runtime_dependency(%q<multi_json>, ["~> 1.0"])
    else
      s.add_dependency(%q<rspec>, ["~> 2.8.0"])
      s.add_dependency(%q<fakeweb>, [">= 1.2"])
      s.add_dependency(%q<rack>, [">= 1.0"])
      s.add_dependency(%q<excon>, [">= 0.6.1"])
      s.add_dependency(%q<rake>, [">= 0"])
      s.add_dependency(%q<i18n>, [">= 0.4.0"])
      s.add_dependency(%q<builder>, [">= 2.1.2"])
      s.add_dependency(%q<beefcake>, ["~> 0.3.7"])
      s.add_dependency(%q<multi_json>, ["~> 1.0"])
    end
  else
    s.add_dependency(%q<rspec>, ["~> 2.8.0"])
    s.add_dependency(%q<fakeweb>, [">= 1.2"])
    s.add_dependency(%q<rack>, [">= 1.0"])
    s.add_dependency(%q<excon>, [">= 0.6.1"])
    s.add_dependency(%q<rake>, [">= 0"])
    s.add_dependency(%q<i18n>, [">= 0.4.0"])
    s.add_dependency(%q<builder>, [">= 2.1.2"])
    s.add_dependency(%q<beefcake>, ["~> 0.3.7"])
    s.add_dependency(%q<multi_json>, ["~> 1.0"])
  end
end

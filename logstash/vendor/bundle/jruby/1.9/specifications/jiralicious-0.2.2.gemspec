# -*- encoding: utf-8 -*-
# stub: jiralicious 0.2.2 ruby lib

Gem::Specification.new do |s|
  s.name = "jiralicious"
  s.version = "0.2.2"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Jason Stewart"]
  s.date = "2013-06-19"
  s.description = "A Ruby library for interacting with JIRA's REST API"
  s.email = "jstewart@fusionary.com"
  s.homepage = "http://github.com/jstewart/jiralicious"
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.rubygems_version = "2.1.9"
  s.summary = "A Ruby library for interacting with JIRA's REST API"

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<crack>, ["~> 0.1.8"])
      s.add_runtime_dependency(%q<httparty>, ["< 0.12.0", ">= 0.10"])
      s.add_runtime_dependency(%q<hashie>, [">= 1.1"])
      s.add_runtime_dependency(%q<json>, ["< 1.9.0", ">= 1.6"])
      s.add_development_dependency(%q<rspec>, ["~> 2.6"])
      s.add_development_dependency(%q<rake>, [">= 0"])
      s.add_development_dependency(%q<fakeweb>, ["~> 1.3.0"])
    else
      s.add_dependency(%q<crack>, ["~> 0.1.8"])
      s.add_dependency(%q<httparty>, ["< 0.12.0", ">= 0.10"])
      s.add_dependency(%q<hashie>, [">= 1.1"])
      s.add_dependency(%q<json>, ["< 1.9.0", ">= 1.6"])
      s.add_dependency(%q<rspec>, ["~> 2.6"])
      s.add_dependency(%q<rake>, [">= 0"])
      s.add_dependency(%q<fakeweb>, ["~> 1.3.0"])
    end
  else
    s.add_dependency(%q<crack>, ["~> 0.1.8"])
    s.add_dependency(%q<httparty>, ["< 0.12.0", ">= 0.10"])
    s.add_dependency(%q<hashie>, [">= 1.1"])
    s.add_dependency(%q<json>, ["< 1.9.0", ">= 1.6"])
    s.add_dependency(%q<rspec>, ["~> 2.6"])
    s.add_dependency(%q<rake>, [">= 0"])
    s.add_dependency(%q<fakeweb>, ["~> 1.3.0"])
  end
end

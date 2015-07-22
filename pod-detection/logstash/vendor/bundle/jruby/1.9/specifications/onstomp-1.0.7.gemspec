# -*- encoding: utf-8 -*-
# stub: onstomp 1.0.7 ruby lib

Gem::Specification.new do |s|
  s.name = "onstomp"
  s.version = "1.0.7"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Ian D. Eccles"]
  s.date = "2012-06-11"
  s.description = "Client library for message passing with brokers that support the Stomp protocol."
  s.email = ["ian.eccles@gmail.com"]
  s.homepage = "http://github.com/meadvillerb/onstomp"
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.8.7")
  s.rubyforge_project = "onstomp-core"
  s.rubygems_version = "2.1.9"
  s.summary = "Client for message queues implementing the Stomp protocol interface."

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<rspec>, ["~> 2.4.0"])
      s.add_development_dependency(%q<simplecov>, [">= 0.3.0"])
      s.add_development_dependency(%q<yard>, [">= 0.6.0"])
      s.add_development_dependency(%q<rake>, [">= 0"])
    else
      s.add_dependency(%q<rspec>, ["~> 2.4.0"])
      s.add_dependency(%q<simplecov>, [">= 0.3.0"])
      s.add_dependency(%q<yard>, [">= 0.6.0"])
      s.add_dependency(%q<rake>, [">= 0"])
    end
  else
    s.add_dependency(%q<rspec>, ["~> 2.4.0"])
    s.add_dependency(%q<simplecov>, [">= 0.3.0"])
    s.add_dependency(%q<yard>, [">= 0.6.0"])
    s.add_dependency(%q<rake>, [">= 0"])
  end
end

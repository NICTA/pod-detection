# -*- encoding: utf-8 -*-
# stub: riemann-client 0.2.1 ruby lib

Gem::Specification.new do |s|
  s.name = "riemann-client"
  s.version = "0.2.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Kyle Kingsbury"]
  s.date = "2013-04-02"
  s.email = "aphyr@aphyr.com"
  s.homepage = "https://github.com/aphyr/riemann-ruby-client"
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.8.7")
  s.rubyforge_project = "riemann-client"
  s.rubygems_version = "2.1.9"
  s.summary = "Client for the distributed event system Riemann."

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<beefcake>, [">= 0.3.5"])
      s.add_runtime_dependency(%q<trollop>, [">= 1.16.2"])
      s.add_runtime_dependency(%q<mtrc>, [">= 0.0.4"])
    else
      s.add_dependency(%q<beefcake>, [">= 0.3.5"])
      s.add_dependency(%q<trollop>, [">= 1.16.2"])
      s.add_dependency(%q<mtrc>, [">= 0.0.4"])
    end
  else
    s.add_dependency(%q<beefcake>, [">= 0.3.5"])
    s.add_dependency(%q<trollop>, [">= 1.16.2"])
    s.add_dependency(%q<mtrc>, [">= 0.0.4"])
  end
end

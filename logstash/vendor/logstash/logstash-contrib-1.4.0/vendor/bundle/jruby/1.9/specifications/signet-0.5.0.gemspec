# -*- encoding: utf-8 -*-
# stub: signet 0.5.0 ruby lib

Gem::Specification.new do |s|
  s.name = "signet"
  s.version = "0.5.0"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Bob Aman"]
  s.date = "2013-05-31"
  s.description = "Signet is an OAuth 1.0 / OAuth 2.0 implementation.\n"
  s.email = "bobaman@google.com"
  s.extra_rdoc_files = ["README.md"]
  s.files = ["README.md"]
  s.homepage = "http://code.google.com/p/oauth-signet/"
  s.rdoc_options = ["--main", "README.md"]
  s.require_paths = ["lib"]
  s.rubygems_version = "2.1.9"
  s.summary = "Signet is an OAuth 1.0 / OAuth 2.0 implementation."

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<addressable>, [">= 2.2.3"])
      s.add_runtime_dependency(%q<faraday>, [">= 0.9.0.rc5"])
      s.add_runtime_dependency(%q<multi_json>, [">= 1.0.0"])
      s.add_runtime_dependency(%q<jwt>, [">= 0.1.5"])
      s.add_development_dependency(%q<rake>, [">= 0.9.0"])
      s.add_development_dependency(%q<rspec>, [">= 2.11.0"])
      s.add_development_dependency(%q<launchy>, [">= 2.1.1"])
    else
      s.add_dependency(%q<addressable>, [">= 2.2.3"])
      s.add_dependency(%q<faraday>, [">= 0.9.0.rc5"])
      s.add_dependency(%q<multi_json>, [">= 1.0.0"])
      s.add_dependency(%q<jwt>, [">= 0.1.5"])
      s.add_dependency(%q<rake>, [">= 0.9.0"])
      s.add_dependency(%q<rspec>, [">= 2.11.0"])
      s.add_dependency(%q<launchy>, [">= 2.1.1"])
    end
  else
    s.add_dependency(%q<addressable>, [">= 2.2.3"])
    s.add_dependency(%q<faraday>, [">= 0.9.0.rc5"])
    s.add_dependency(%q<multi_json>, [">= 1.0.0"])
    s.add_dependency(%q<jwt>, [">= 0.1.5"])
    s.add_dependency(%q<rake>, [">= 0.9.0"])
    s.add_dependency(%q<rspec>, [">= 2.11.0"])
    s.add_dependency(%q<launchy>, [">= 2.1.1"])
  end
end

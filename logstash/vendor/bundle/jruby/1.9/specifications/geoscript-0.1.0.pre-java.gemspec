# -*- encoding: utf-8 -*-
# stub: geoscript 0.1.0.pre java lib

Gem::Specification.new do |s|
  s.name = "geoscript"
  s.version = "0.1.0.pre"
  s.platform = "java"

  s.required_rubygems_version = Gem::Requirement.new("> 1.3.1") if s.respond_to? :required_rubygems_version=
  s.authors = ["Scooter Wadsworth"]
  s.date = "2014-01-25"
  s.description = "GeoScript for JRuby - makes using GeoTools from JRuby easier and more fun."
  s.email = ["scooterwadsworth@gmail.com"]
  s.homepage = "https://github.com/scooterw/geoscript-ruby"
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.rubygems_version = "2.1.9"
  s.summary = "GeoScript is a library for making use of GeoTools from JRuby easier and more fun."

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<geotools-jars>, [">= 8.0"])
      s.add_development_dependency(%q<rake>, [">= 0"])
      s.add_development_dependency(%q<rspec>, [">= 0"])
      s.add_development_dependency(%q<guard-rspec>, [">= 0"])
    else
      s.add_dependency(%q<geotools-jars>, [">= 8.0"])
      s.add_dependency(%q<rake>, [">= 0"])
      s.add_dependency(%q<rspec>, [">= 0"])
      s.add_dependency(%q<guard-rspec>, [">= 0"])
    end
  else
    s.add_dependency(%q<geotools-jars>, [">= 8.0"])
    s.add_dependency(%q<rake>, [">= 0"])
    s.add_dependency(%q<rspec>, [">= 0"])
    s.add_dependency(%q<guard-rspec>, [">= 0"])
  end
end

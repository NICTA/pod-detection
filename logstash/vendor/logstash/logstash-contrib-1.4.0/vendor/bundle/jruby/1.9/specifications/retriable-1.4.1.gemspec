# -*- encoding: utf-8 -*-
# stub: retriable 1.4.1 ruby lib

Gem::Specification.new do |s|
  s.name = "retriable"
  s.version = "1.4.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Jack Chu"]
  s.date = "2013-09-06"
  s.description = "Retriable is an simple DSL to retry a code block if an exception should be raised. This is especially useful when interacting external api/services or file system calls."
  s.email = ["jack@jackchu.com"]
  s.homepage = "http://github.com/kamui/retriable"
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.rubyforge_project = "retriable"
  s.rubygems_version = "2.1.9"
  s.summary = "Retriable is an simple DSL to retry a code block if an exception should be raised."

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<rake>, [">= 0"])
      s.add_development_dependency(%q<minitest>, [">= 5.0"])
    else
      s.add_dependency(%q<rake>, [">= 0"])
      s.add_dependency(%q<minitest>, [">= 5.0"])
    end
  else
    s.add_dependency(%q<rake>, [">= 0"])
    s.add_dependency(%q<minitest>, [">= 5.0"])
  end
end

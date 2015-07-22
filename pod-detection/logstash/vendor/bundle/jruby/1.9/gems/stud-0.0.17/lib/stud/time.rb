module Stud
  module Time
    # The following table is copied from joda-time's docs
    # http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html
    # Symbol  Meaning                      Presentation  Examples
    # ------  -------                      ------------  -------
    # G       era                          text          AD
    # C       century of era (>=0)         number        20
    # Y       year of era (>=0)            year          1996

    # x       weekyear                     year          1996
    # w       week of weekyear             number        27
    # e       day of week                  number        2
    # E       day of week                  text          Tuesday; Tue

    # y       year                         year          1996
    # D       day of year                  number        189
    # M       month of year                month         July; Jul; 07
    # d       day of month                 number        10

    # a       halfday of day               text          PM
    # K       hour of halfday (0~11)       number        0
    # h       clockhour of halfday (1~12)  number        12

    # H       hour of day (0~23)           number        0
    # k       clockhour of day (1~24)      number        24
    # m       minute of hour               number        30
    # s       second of minute             number        55
    # S       fraction of second           number        978

    # z       time zone                    text          Pacific Standard Time; PST
    # Z       time zone offset/id          zone          -0800; -08:00; America/Los_Angeles

    # '       escape for text              delimiter
    # ''      single quote                 literal       '
  end
end

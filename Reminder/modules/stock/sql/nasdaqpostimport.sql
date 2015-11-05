#
update NasdaqEarnAnnounce ea, NasdaqEarnAnnounceTime eat set ea.announceTime = eat.announceTime where ea.stockid=eat.stockid and ea.dt=eat.dt and ea.announceTime is null

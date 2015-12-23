setwd("C:/mydoc/myprojects/ereminder/Reminder/modules/stock/output")
mydata = read.csv("2015_earn_forecast_summary.csv")
mydata = mydata[complete.cases(mydata),]
mydata[,c(2:3)] <- sapply(mydata[,c(2:3)], as.numeric)
mean(mydata[[3]])
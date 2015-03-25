  function setUpPreference() {
    var preference=document.preference;
    // begin preference
    preference.radioIndex=1;
    preference.breaktag=0;
    preference.isLargeSchema=true;
    preference.checkSchema=false;
    preference.obeySchema=false;
    preference.scriptLoc="server";
    // end preference
  }

  function setUpSchemaInfo() {
	// begin schema
	  setSchema('complexType,BrowseDetailType,firstPageClickStream',{'#dt':'ClickStreamType','#ph':'complexType,ClickStreamType,','#dc':'the click stream to the 1st page of the book','#cm':'(<link>)+(<finishCondition>)?'});
	  setSchema('complexType,ValueType,sampleUrl',{'#dt':'#string','#dc':'multiple sample provided for toType is Page or Pagelist'});
	  setSchema('complexType,SubListType,@isLeaf',{'#df':'true','#dt':'#boolean'});
	  setSchema('complexType,BinaryBoolOp,@lhs',{'#dt':'#string'});
	  setSchema('complexType,BrowseTaskType,idUrlMappingFirstPage',{'#dt':'RegExpType','#ph':'complexType,RegExpType,','#cm':'(<Token>)+'});
	  setSchema('complexType,ValueType,@fromType',{'#dt':'VarType','#em':['string','int','float','xpath','date','page','list','pagelist','regexp','param','const'],'#dc':'the value\'s input type, default to xpath'});
	  setSchema('complexType,ValueType,@toType',{'#dt':'VarType','#em':['string','int','float','xpath','date','page','list','pagelist','regexp','param','const'],'#dc':'the type of the interpret result'});
	  setSchema('complexType,ValueType,@format',{'#dt':'#string','#dc':'used with toType=date'});
	  setSchema('complexType,ValueType,@enableJS',{'#dt':'#boolean','#dc':'used with toType=page, if null, reusing previous wc setting'});
	  setSchema('complexType,TasksType,@maxThread',{'#df':'0','#dt':'#int','#dc':'max # threads can be used to crawl this site, default 0 for unlimited'});
	  setSchema('complexType,BrowseTaskType,param',{'#dt':'ParamType','#ph':'complexType,ParamType,','#cm':'(<@name>)(<@type>)?(<@value>)?'});
	  setSchema('complexType,SubListType,@nextPage',{'#dt':'#string'});
	  setSchema('complexType,SubListType,itemList',{'#dt':'ValueType','#ph':'complexType,ValueType,','#dc':'xpath to list of item, usually of type html element , but can be page or further click stream (TBD), then itemFullUrl is the abs xpath on the last page','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,BrowseTaskType,itemName',{'#dt':'ValueType','#ph':'complexType,ValueType,','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,BrowseCatType,itemPerPage',{'#dt':'ValueType','#ph':'complexType,ValueType,','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,BrowseTaskType,idUrlMapping',{'#dt':'RegExpType','#ph':'complexType,RegExpType,','#dc':'for bdt: 1. this is uni-directional, from url to id, 2. if not having this, id will be assigned in user attribute','#cm':'(<Token>)+'});
	  setSchema('complexType,SubListType,userAttribute',{'#dt':'NameValueType','#ph':'complexType,NameValueType,','#cm':'(<@name>)(<@nameType>)?(<value>)'});
	  setSchema('complexType,BrowseTaskType,@itemType',{'#dt':'#string'});
	  setSchema('complexType,ClickType,input',{'#dt':'NameValueType','#ph':'complexType,NameValueType,','#dc':'parameters to input','#cm':'(<@name>)(<@nameType>)?(<value>)'});
	  setSchema('complexType,BrowseTaskType,userAttribute',{'#dt':'NameValueType','#ph':'complexType,NameValueType,','#cm':'(<@name>)(<@nameType>)?(<value>)'});
	  setSchema('complexType,BrowseCatType,totalItemNum',{'#dt':'ValueType','#ph':'complexType,ValueType,','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,TaskInvokeType,@toCallTaskName',{'#dt':'#string'});
	  setSchema('complexType,TaskInvokeType,@rerunInterim',{'#dt':'#int','#dc':'overrides that defined on Task'});
	  setSchema('complexType,BrowseCatType,subItemList',{'#dt':'SubListType','#ph':'complexType,SubListType,','#cm':'(<@itemFullUrl>)?(<@nextPage>)?(<@isLeaf>)?(<@lastItem>)?(<itemList>)(<userAttribute>)*(<lastPageCondition>)?(<name>)?'});
	  setSchema('complexType,BrowseDetailType,@nextPage',{'#dt':'#string'});
	  setSchema('complexType,BrowseDetailType,baseBrowseTask',{'#dt':'BrowseTaskType','#ph':'complexType,BrowseTaskType,','#cm':'(<@taskName>)?(<@itemType>)?(<@isStart>)?(<@rerunInterim>)?(<@enableJS>)?(<@startUrl>)?(<@skipJS>)?(<@highMem>)?(<sampleUrl>)*(<param>)*(<userAttribute>)*(<idUrlMapping>)?(<idUrlMappingFirstPage>)?(<itemName>)?'});
	  setSchema('complexType,BrowseCatType,@isLeaf',{'#dt':'#boolean'});
	  setSchema('complexType,ValueType,strPreprocess,@trimPre',{'#dt':'#string'});
	  setSchema('complexType,ClickStreamType,finishCondition',{'#dt':'BinaryBoolOp','#ph':'complexType,BinaryBoolOp,','#dc':'if specified, the last link will be clicked till\nfinish condition, if true reach the first page\nof the book','#cm':'(<@lhs>)?(<@rhs>)?(<@operator>)?'});
	  setSchema('complexType,BrowseDetailType,@imagePath',{'#dt':'#string'});
	  setSchema('complexType,ParamType,@type',{'#df':'string','#dt':'VarType','#em':['string','int','float','xpath','date','page','list','pagelist','regexp','param','const']});
	  setSchema('complexType,BrowseDetailType,lastPageCondition',{'#dt':'BinaryBoolOp','#ph':'complexType,BinaryBoolOp,','#dc':'if true reach the end of the book','#cm':'(<@lhs>)?(<@rhs>)?(<@operator>)?'});
	  setSchema('complexType,BrowseTaskType,@rerunInterim',{'#dt':'#int','#dc':'if missing, means run only, else specify number of seconds before rerun'});
	  setSchema('complexType,TasksType,PrdTask',{'#dt':'BrowseDetailType','#ph':'complexType,BrowseDetailType,','#cm':'(<@imagePath>)?(<@nextPage>)?(<baseBrowseTask>)(<lastPageCondition>)?(<firstPageClickStream>)?(<totalPage>)?(<directPages>)?'});
	  setSchema('complexType,TokenType,@name',{'#dt':'#string'});
	  setSchema('complexType,TasksType,invokeTask',{'#dt':'TaskInvokeType','#ph':'complexType,TaskInvokeType,','#cm':'(<@toCallTaskName>)?(<@rerunInterim>)?(<@myTaskName>)?(<paramList>)*'});
	  setSchema('complexType,BrowseCatType,totalPageNum',{'#dt':'ValueType','#ph':'complexType,ValueType,','#dc':'means how many pages of childitems we have, each page will be split in one task. \nif not set, but itemPerPage and totalItemNum set, this can be calculated.\nif also not set, then 1 task browsing all items will be generated.','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,BrowseTaskType,@isStart',{'#df':'false','#dt':'#boolean','#dc':'true, started when engine starts, does not need an invoke'});
	  setSchema('complexType,ClickStreamType,link',{'#dt':'ClickType','#ph':'complexType,ClickType,','#dc':'usually toType of nextpage is page, and name will be used in later expression','#cm':'(<input>)*(<nextpage>)'});
	  setSchema('complexType,BrowseCatType,baseBrowseTask',{'#dt':'BrowseTaskType','#ph':'complexType,BrowseTaskType,','#cm':'(<@taskName>)?(<@itemType>)?(<@isStart>)?(<@rerunInterim>)?(<@enableJS>)?(<@startUrl>)?(<@skipJS>)?(<@highMem>)?(<sampleUrl>)*(<param>)*(<userAttribute>)*(<idUrlMapping>)?(<idUrlMappingFirstPage>)?(<itemName>)?'});
	  setSchema('complexType,RegExpType,Token',{'#dt':'TokenType','#ph':'complexType,TokenType,','#cm':'(<@name>)(<@value>)(<@type>)?'});
	  setSchema('complexType,BinaryBoolOp,@rhs',{'#dt':'#string'});
	  setSchema('complexType,BrowseDetailType,directPages',{'#dt':'ValueType','#ph':'complexType,ValueType,','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,ParamValueType,@paramName',{'#dt':'#string'});
	  setSchema('Tasks',{'#dt':'TasksType','#ph':'complexType,TasksType,','#cm':'(<@storeId>)(<@rootVolume>)(<@maxThread>)?(<skipUrl>)*(<CatTask>)*(<PrdTask>)?(<invokeTask>)*'});
	  setSchema('complexType,BrowseTaskType,@highMem',{'#df':'false','#dt':'#boolean'});
	  setSchema('complexType,SubListType,name',{'#dt':'ValueType','#ph':'complexType,ValueType,','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,TasksType,CatTask',{'#dt':'BrowseCatType','#ph':'complexType,BrowseCatType,','#cm':'(<@isLeaf>)(<baseBrowseTask>)(<subItemList>)(<totalPageNum>)?(<itemPerPage>)?(<totalItemNum>)?'});
	  setSchema('complexType,TokenType,@value',{'#dt':'#string'});
	  setSchema('complexType,BrowseTaskType,@enableJS',{'#df':'false','#dt':'#boolean'});
	  setSchema('complexType,BinaryBoolOp,@operator',{'#dt':'OpType','#em':['!=','=']});
	  setSchema('complexType,NameValueType,@name',{'#dt':'#string'});
	  setSchema('complexType,TasksType,skipUrl',{'#dt':{'#dt':'#string','#ct':'v.length <= 100'}});
	  setSchema('complexType,TaskInvokeType,@myTaskName',{'#dt':'#string'});
	  setSchema('complexType,NameValueType,value',{'#dt':'ValueType','#ph':'complexType,ValueType,','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,SubListType,lastPageCondition',{'#dt':'BinaryBoolOp','#ph':'complexType,BinaryBoolOp,','#cm':'(<@lhs>)?(<@rhs>)?(<@operator>)?'});
	  setSchema('complexType,TaskInvokeType,paramList,param',{'#dt':'ParamValueType','#ph':'complexType,ParamValueType,','#cm':'(<@paramName>)?(<@value>)?'});
	  setSchema('complexType,BrowseTaskType,@skipJS',{'#dt':'#string'});
	  setSchema('complexType,ValueType,strPreprocess,@trimPost',{'#dt':'#string'});
	  setSchema('complexType,ValueType,strPreprocess',{'#ph':'complexType,ValueType,strPreprocess,','#cm':'(<@trimPre>)?(<@trimPost>)?'});
	  setSchema('complexType,TokenType,@type',{'#df':'const','#dt':'VarType','#em':['string','int','float','xpath','date','page','list','pagelist','regexp','param','const'],'#dc':'optional, if missing means false, if true means the value is regExp, does not need to quote. if false or not set, it will be quoted'});
	  setSchema('complexType,TasksType,@rootVolume',{'#dt':'#string'});
	  setSchema('complexType,NameValueType,@nameType',{'#dt':'VarType','#em':['string','int','float','xpath','date','page','list','pagelist','regexp','param','const'],'#dc':'for input/set, the nameType defaults to XPATH, for get the nameType defaults to string'});
	  setSchema('complexType,BrowseTaskType,@taskName',{'#dt':'#string','#dc':'if not specified, then this task can\'t be invoked explicitly'});
	  setSchema('complexType,BrowseDetailType,totalPage',{'#dt':'ValueType','#ph':'complexType,ValueType,','#cm':'(<@fromType>)?(<@value>)(<@toType>)?(<@basePage>)?(<@enableJS>)?(<@format>)?(<strPreprocess>)?(<sampleUrl>)*'});
	  setSchema('complexType,SubListType,@itemFullUrl',{'#dt':'#string'});
	  setSchema('complexType,TaskInvokeType,paramList',{'#ph':'complexType,TaskInvokeType,paramList,','#cm':'(<param>)*'});
	  setSchema('complexType,SubListType,@lastItem',{'#dt':'#string'});
	  setSchema('complexType,ClickType,nextpage',{'#dt':'NameValueType','#ph':'complexType,NameValueType,','#cm':'(<@name>)(<@nameType>)?(<value>)'});
	  setSchema('complexType,BrowseTaskType,@startUrl',{'#dt':'#string','#dc':'if isStart is true, then this is needed.'});
	  setSchema('complexType,ValueType,@value',{'#dt':'#string'});
	  setSchema('complexType,ParamValueType,@value',{'#dt':'#string'});
	  setSchema('complexType,ValueType,@basePage',{'#dt':'#string','#dc':'based on which page, to eval this xpath'});
	  setSchema('complexType,TasksType,@storeId',{'#dt':'#string'});
	  setSchema('complexType,ParamType,@value',{'#dt':'#string','#dc':'design time the default value, runtime will be assigned by taskInvoker'});
	  setSchema('complexType,ParamType,@name',{'#dt':'#string'});
	  setSchema('complexType,BrowseTaskType,sampleUrl',{'#dt':'#string','#dc':'multiple sample url for start url'});
	  setTypeDef('TokenType',{'#ph':'complexType,TokenType,','#dc':'for Id-Url mapping'});
	  setTypeDef('ClickStreamType',{'#ph':'complexType,ClickStreamType,','#dc':'For each click/link, first do some input assignment, then click the xpath typed nextpage and return a page.'});
	  setTypeDef('OpType',{'#dt':{'#dt':'#string','#em':['!=','=']}});
	  setTypeDef('VarType',{'#dt':{'#dt':'#string','#em':['string','int','float','xpath','date','page','list','pagelist','regexp','param','const']}});
	      // end schema
}
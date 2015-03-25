function makeElementChosen(selectObj) {
  var menuCommand = selectObj.value;
  selectObj.selectedIndex = 0;
  selectObj.blur();
  document.xmlForm.scrap = makeElementFromMenu(menuCommand);
  return false;
}

//Cheng Yi
function makeElementFromMenu(menuValue) {
	var elementType = menuValue.charAt(0);
	if (elementType != '#') {//Attribute or SimpleType
		node = getPrototype(menuValue);
		//Add begin -- Cheng Yi
		metaInfo = guessMetaInfo(menuValue);
		if (metaInfo){
			var defValue = metaInfo['#df'];
			if (defValue) {
				// we know it is input is either text field or area
				getInputOfItem(node).value = defValue;
			}
			var options = metaInfo['#em'];
			if (options) {
				becomeSelectListElm(node, options);
			}
		}
		//<span class="XMLAttribute" onclick="selectelm(this, event);" ondblclick="selectelm(this, event);">
		//	<span class="@startUrl">
		//	<label>startUrl: </label>
		//	<input type="hidden" value="@startUrl" name=".tg">
		//	<div class="cy-textarea-container">
		//		<textarea oninput="textAreaAutoSize(this)" name="@startUrl" value="" "></textarea>
		//		<div class="cy-textarea-size"></div>
		//	</div>
		//	</span>
		//	<br>
		//</span>
		var textarea = getElementByXpath("./span[1]/div[1]/textarea[1]", node);
		if (textarea!=null && (textarea.name === '@startUrl' || textarea.name === '#sampleUrl')){
			addSubmitForUrl(textarea);  
		}
		return node;
  }else{//Element
	  return makeKnownElement(menuValue.substr(1));
  }
}

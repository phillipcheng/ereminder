
// Copyright 2004-05 by Edmund K. Lai

// TBD : convert to method so we can have inheritance

function notValid_anySimpleType(strObj) {
  return false;
}

function notValid_anyType(strObj) {
  return false;
}

function canonicalized(strObj) {
  strObj.length = strObj.value.length;
  strObj.canonicalized = true;
  return true;
}

function notValid_hexBinary(strObj) {
  strObj.value = strObj.value.replace(/\s/g, '').toUpperCase();
  canonicalized(strObj);
  var bool = (strObj.value.search(/^([0-9A-F]{2})*$/) != 0);
  strObj.length = strObj.length / 2;
  return bool;
}

function canonical_hexBinary(strObj) {
  notValid_hexBinary(strObj);
  return true;
}

function notValid_base64Binary(strObj) {
  strObj.value = strObj.value.replace(/\s/g, '');
  canonicalized(strObj);
  var bool = (strObj.value.search(/^(([0-9a-zA-Z\/+]{4})*)*([0-9a-zA-Z\/+]{3}=|[0-9a-zA-Z\/+]{2}==)?$/) != 0);
  strObj.length = strObj.length * 3 / 4; // TBD we need to find the actual number of bytes
  return bool;
}

function canonical_base64Binary(strObj) {
  notValid_base64Binary(strObj);
  return true;
}

function notValid_decimal(strObj) {
  return (strObj.value.search(/^\s*[+-]?(\d+\.?\d*|\.\d+)\s*$/) != 0);
}

function canonical_decimal(strObj) {
  var rst = strObj.value.replace(/^\s*([+-]?)0*/, "$1"); // remove leading space and 0
  rst = rst.replace(/^([+-]?)\./, "$1" + "0."); // make sure a digit before decmial pt
  rst = rst.replace(/\s+$/, ''); // trim it
  if (rst.indexOf('.')) { // has decimal pt
    rst = rst.replace(/0*$/, ''); // remove trailing 0
    if (rst.charAt(rst.length-1) == '.') rst += '0';
  } else {
    rst += '.0';
  }
  if (rst[rst.length-1] == '.') rst = rst.substr(0,rst.length-1);
  if (rst == '+' || rst == '-' || rst == '') { 
    rst = '0.0';
  } else if (rst[0] == '+') {
    rst = rst.substr(1);
  }
  var totalDigits = rst.length - 1;
  var fractionDigits = totalDigits - rst.indexOf('.');
  if (rst.charAt(rst.length-1) == '0') {
    fractionDigits--;
    totalDigits--;
  }
  if (rst.charAt(0) == '-') totalDigits--;
  strObj.value = rst;
  strObj.fractionDigits = fractionDigits;
  strObj.totalDigits = totalDigits;
  return canonicalized(strObj);
}

function notValid_integer(strObj) {
  return (strObj.value.search(/^\s*[+-]?\d+\s*$/) != 0);
}

function canonical_integer(strObj) {
  // we cannot use parseInt because parseInt(010) == 8
  var rst = strObj.value.match(/^\s*0*([+-]?)0*([\d]*)/);
  if (rst[1] == '+') rst[1] = '';
  if (rst[2]) {
    strObj.value = rst[1] + rst[2]; // should we make it into a number
    strObj.totalDigits = rst[2].length;
    strObj.fractionDigits = 0;
    return canonicalized(strObj);
  }
  strObj.value = 0;
  strObj.totalDigits = 0;
  strObj.fractionDigits = 0;
  return canonicalized(strObj);
}

function notValid_nonPositiveInteger(strObj) {
  return (notValid_integer(strObj) || parseInt(strObj.value) > 0);
}

function canonical_nonPositiveInteger(strObj) {
  return canonical_integer(strObj);
}

function notValid_negativeInteger(strObj) {
  return (notValid_integer(strObj) || parseInt(strObj.value) >= 0);
}

function canonical_negativeInteger(strObj) {
  return canonical_integer(strObj);
}

function notValid_nonNegativeInteger(strObj) {
  return (notValid_integer(strObj) || parseInt(strObj.value) < 0);
}

function canonical_nonNegativeInteger(strObj) {
  return canonical_integer(strObj);
}

function notValid_positiveInteger(strObj) {
  return (notValid_integer(strObj) || parseInt(strObj.value) <= 0);
}

function canonical_positiveInteger(strObj) {
  return canonical_integer(strObj);
}

function notValid_long(strObj) {
  if (notValid_integer(strObj)) return true;
  return (strObj.value < -9223372036854775808 || strObj.value > 9223372036854775807);
}

function canonical_long(strObj) {
  return canonical_integer(strObj);
}

function notValid_int(strObj) {
  if (notValid_integer(strObj)) return true;
  return (strObj.value <= -2147483649 || strObj.value >= 2147483648);
}

function canonical_int(strObj) {
  return canonical_integer(strObj);
}

function notValid_short(strObj) {
  if (notValid_integer(strObj)) return true;
  return (strObj.value <= -32769 || strObj.value >= 32768);
}

function canonical_short(strObj) {
  return canonical_integer(strObj);
}

function notValid_byte(strObj) {
  if (notValid_integer(strObj)) return true;
  return (strObj.value <= -129 || strObj.value >= 128);
}

function canonical_byte(strObj) {
  return canonical_integer(strObj);
}

function notValid_unsignedLong(strObj) {
  if (notValid_integer(strObj)) return true;
  return (strObj.value < 0 || strObj.value >= 18446744073709551616);
}

function canonical_unsignedLong(strObj) {
  return canonical_integer(strObj);
}

function notValid_unsignedInt(strObj) {
  if (notValid_integer(strObj)) return true;
  return (strObj.value < 0 || strObj.value >= 4294967296);
}

function canonical_unsignedInt(strObj) {
  return canonical_integer(strObj);
}

function notValid_unsignedShort(strObj) {
  if (notValid_integer(strObj)) return true;
  return (strObj.value < 0 || strObj.value >= 65536);
}

function canonical_unsignedShort(strObj) {
  return canonical_integer(strObj);
}

function notValid_unsignedByte(strObj) {
  if (notValid_integer(strObj)) return true;
  return (strObj.value < 0 || strObj.value >= 256);
}

function canonical_unsignedByte(strObj) {
  return canonical_integer(strObj);
}

function notValidFloatOrDouble(strObj,lowExp,highExp) {
  if (strObj.value.search(/^(NaN|INF|-INF)$/) == 0) return false;
  if (isNaN(parseFloat(strObj.value))) return true;
  var rst = strObj.value.match(/^[+-]?(?:\d*)?\.?(?:\d*)?(?:[eE]([+-]?\d+))?$/);
  if (rst) {
    if (rst[1] && (rst[1] < lowExp || rst[1] > highExp)) return 'Exponent is out of range';
    return false;
  }
  return true;
}

function notValid_double(strObj) {
  return notValidFloatOrDouble(strObj, -323, 308);
}

function canonical_double(strObj) {
  var rst = strObj.value.match(/^\s*(NaN|INF|-INF)\s*$/);
  var totalDigits, fractionDigits;
  if (rst) return rst[1];
  rst = parseFloat(strObj.value).toString().replace('e','E');
  var decLoc = rst.indexOf('.');
  var expLoc = rst.indexOf('E');
  var exp = 0;
  if (expLoc != -1) exp = parseInt(rst.substr(expLoc+1));
  if (decLoc == -1) { // no decimal pt
    fractionDigits = 0;
    if (expLoc == -1) { // no exp
      totalDigits = rst.length;
      rst = rst + '.0';
    } else {
      totalDigits = expLoc;
      rst = rst.substr(0,expLoc) + '.0' + rst.expLoc(expLoc);
    }
  } else {
    if (expLoc == -1) {
      expLoc = rst.length;
    } else {
      exp = parseInt(rst.substr(expLoc+1));
    }
    fractionDigits = expLoc - decLoc - 1;
    totalDigits = decLoc + fractionDigits;
  }
  fractionDigits = fractionDigits - exp;
  if (fractionDigits < 0) fractionDigits = 0;
  if (rst.charAt(0) == '-') totalDigits--;
  strObj.value = rst;
  strObj.value.totalDigits = totalDigits;
  strObj.value.fractionDigits = fractionDigits;
  return canonicalized(strObj);
}

function notValid_float(strObj) {
  return notValidFloatOrDouble(strObj, -44, 38);
}

function canonical_float(strObj) {
  return canonical_double(strObj);
}

function notValid_boolean(strObj) {
  return (strObj.value.search(/^\s*(true|false|0|1)\s*$/) != 0);
}

function canonical_boolean(strObj) {
  strObj.value = (strObj.value.search(/^\s*(true|1)\s*$/) == 0);
  return canonicalized(strObj);
}

function checkMonthDay(year, month, day) {
  if (year == 0) return false;
  if (month == 0 || month > 12) return false;
  if (month == 2 && day == 29) {
    if (year < 0) year++;
    return !(year % 4 && ( !(year % 100) || year % 4000 )); // OK if leap year
  }
  var daysInMonth = [31,28,31,30,31,30,31,31,30,31,30,31];
  if (day == 0 || day > daysInMonth[month-1]) return false;
  return true;
}

function checkTZ(hour) {
  return (hour == undefined || hour <= 14);
}

function notValid_dateTime(strObj) {
  var rst = strObj.value.match(/^\s*(-?\d{4,})-(\d\d)-(\d\d)T(\d\d):[0-5]\d:[0-5]\d(?:\.\d*)?(?:Z|[+-](\d\d):[0-5]\d)?\s*$/);
  if (!rst) return true;
  if (!checkMonthDay(rst[1], rst[2], rst[3])) return true;
  if (rst[4] == 0 || rst[4] > 24) return true;
  if (!checkTZ(rst[5])) return true;
  return false;
}

function notValid_date(strObj) {
  var rst = strObj.value.match(/^\s*(-?\d{4,})-(\d\d)-(\d\d)(?:Z|[+-](\d\d):[0-5]\d)?\s*$/);
  if (!rst) return true;
  if (!checkMonthDay(rst[1], rst[2], rst[3])) return true;
  if (!checkTZ(rst[4])) return true;
  return false;
}

function notValid_gYearMonth(strObj) {
  var rst = strObj.value.match(/^\s*-?\d{4,}-(\d\d)(?:Z|[+-](\d\d):[0-5]\d)?\s*$/);
  if (!rst) return true;
  if (rst[1] == 0 || rst[1] > 12) return true;
  if (!checkTZ(rst[2])) return true;
  return false;
}

function notValid_gYear(strObj) {
  var rst = strObj.value.match(/^\s*-?(\d{4,})(?:Z|[+-](\d\d):[0-5]\d)?\s*$/);
  if (!rst) return true;
  if (rst[1] == 0) return true;
  if (!checkTZ(rst[2])) return true;
  return false;
}

function notValid_time(strObj) {
  var rst = strObj.value.match(/^\s*(\d\d):[0-5]\d:[0-5]\d(?:\.\d*)?(?:Z|[+-](\d\d):[0-5]\d)?\s*$/);
  if (!rst) return true;
  if (rst[1] == 0 || rst[1] > 24) return true;
  if (!checkTZ(rst[2])) return true;
  return false;
}

function notValid_gDay(strObj) {
  var rst = strObj.value.match(/^\s*---([0-3]\d)(?:Z|[+-](\d\d):[0-5]\d)?\s*$/);
  if (!rst) return true;
  if (rst[1] == 0 || rst[1] > 31) return true;
  if (!checkTZ(rst[2])) return true;
  return false;
}

function notValid_gMonthDay(strObj) {
  var rst = strObj.value.match(/^\s*--([01]\d)-([0-3]\d)(?:Z|[+-](\d\d):[0-5]\d)?\s*$/);
  if (!rst) return true;
  if (!checkMonthDay(4, rst[1], rst[2])) return true;
  if (!checkTZ(rst[3])) return true;
  return false;
}

function notValid_gMonth(strObj) {
  var rst = strObj.value.match(/^\s*--([01]\d)(?:Z|[+-](\d\d):[0-5]\d)?\s*$/);
  if (!rst) return true;
  if (rst[1] == 0 || rst[1] > 12) return true;
  if (!checkTZ(rst[2])) return true;
  return false;
}

function notValid_duration(strObj) {
  var rst = strObj.value.match(/^\s*-?P(?:\d+Y)?(?:\d+M)?(?:\d+D)?(?:T(?:\d+H)?(?:\d+M)?(?:\d+(?:\.\d+)?S)?)?\s*$/);
  if (!rst) return true;
  return false;
}

function notValid_string(strObj) {
  return false;
}

function notValid_normalizedString(strObj) {
  strObj.value = strObj.value.replace(/\s/g, ' ');
  return false;
}

function collapseStr(str) {
  return str.replace(/\s+/g, ' ').replace(/^ /, '').replace(/ $/, '');
}

function notValid_token(strObj) {
  strObj.value = collapseStr(strObj.value);
  return false;
}

function notValid_language(strObj) {
  // TBD : to bw written
  return notValid_token(strObj);
}

function notValid_NMTOKEN(strObj) {
  notValid_token(strObj);
  return (strObj.value.search(/^[A-Za-z0-9:_.-]+$/) != 0);
}

function notValid_NMTOKENS(strObj, idInfo) {
  return !checkValidList(strObj, null, '#NMTOKEN', idInfo);
}

function notValid_Name(strObj) {
  notValid_token(strObj);
  return (strObj.value.search(/^[A-Za-z:_][A-Za-z0-9:_.-]*$/) != 0);
}

function notValid_NCName(strObj) {
  notValid_token(strObj);
  return (strObj.value.search(/^[A-Za-z_][A-Za-z0-9_.-]*$/) != 0);
}

function notValid_ID(strObj, idInfo) {
  if (!notValid_NCName(strObj)) {
    if (idInfo) {
      if (idInfo[0][strObj.value]) return 'The ID ' + doubleQuote(strObj.value) + ' is not unique.';
      idInfo[0][strObj.value] = 1;
      if (idInfo[1][strObj.value]) delete idInfo[1][strObj.value];
    }
    return false;
  }
  return true;
}

function notValid_IDREF(strObj, idInfo) {
  if (!notValid_NCName(strObj)) {
    if (idInfo) {
        if (!idInfo[0][strObj.value]) {
        idInfo[1][strObj.value] = 1;
      }
    }
    return false;
  }
  return true;
}

function notValid_IDREFS(strObj, idInfo) {
  return !checkValidList(strObj, null, '#IDREF', idInfo);
}

function notValid_anyURI(strObj) {
  var str = strObj.value.replace(/^\s*/, '').replace(/\s*$/, ''); // trim it
  strObj.value = escape(str).replace('%3A', ':');
  canonicalized(strObj);
  return (strObj.value.search(/^\s*.+?:\/\/.+/) != 0); // has slash slash
}

function matchPattern(v, metaElm) {
  var pattern = metaElm ? metaElm['#pt'] : null;
  if (pattern) {
    if (v.value.search('^' + pattern + '$') != 0)
      return doubleQuote(v.value) + ' does not match the pattern ' + doubleQuote(pattern) + '.';
  }
  return '';
}

function passRestriction(v, metaElm, typeName) {
  if (typeName) // convert from lexical space to value space
    getCanonicalValue(v, typeName);
  var enumeration = metaElm ? metaElm['#em'] : null;
  if (enumeration) {
    var matched = false;
    for (var i=0;i<enumeration.length;i++) {
      if (v.value == enumeration[i]) {
        matched = true;
        break;
      }
    }
    if (!matched)
      return v.value + ' is not one of the choices in ' + doubleQuote(enumeration);
  }
  var constraint = metaElm ? metaElm['#ct'] : null;
  if (constraint) {
    try {
      var rst;
      eval('rst='+constraint);
      if (!rst) return 'v.value = ' + doubleQuote(v.value) + ' does not satisify the constraint ' + singleQuote(constraint) + '.';
    } catch(err) {
      return err + ' in constraint ' + singleQuote(constraint);
    }
  } else if (metaElm && metaElm['#lc']) {
    return checkValidListOfType(v, metaElm, '');
  }
  return ''; 
}

function makeValueObj(str) {
  this.value = str;
  return (this);
}

function checkValidType(valueObj, metaElm, typeName, idInfo) {
  var error = matchPattern(valueObj, metaElm);
  if (error) return error;
  if (typeof(typeName) == 'object') {
    var dataSrc = (metaElm['#lt']) ? metaElm['#lt'] : metaElm['#dt'];
    error = checkValidType(valueObj, dataSrc, dataSrc['#dt'], idInfo);
    if (error) return error;
  } else if (typeName == '#union') {
    var union = metaElm['#un'];
    if (union) {
      var oneError = '';
      for (var i=0;i<union.length;i++) { // check thru each item in union, pass if 1 pass
        oneError = checkValidValue(valueObj, union[i], idInfo);
        if (!oneError) break;
        error += oneError + '\n';
      }
      if (oneError) return error;
    }
  } else if (typeName == '#list') {
    return checkValidListOfType(valueObj, metaElm, metaElm['#lt'], idInfo);
  } else if (typeName.charAt(typeName.length-1) == '*') {
    return checkValidListOfType(valueObj, metaElm, typeName.substr(0,typeName.length-1), idInfo);
  } else if (typeName) {
    if (typeName.charAt(0) == '#') {
      typeName = typeName.substr(1);
      error = eval('notValid_' + typeName + '(valueObj, idInfo)');
    } else {
      var userType = document.schema.datatype[typeName];
      if (userType) {
        metaElm = userType;
      var baseType = metaElm['#dt'];
      error = checkValidType(valueObj, metaElm, baseType, idInfo);
      }
    }
    if (error) {
      if (typeof(error) != 'string') error = '';
      var newError = doubleQuote(valueObj.value) + ' is not a valid ' + typeName + ' type.';
      error += (error == newError) ? "\n" : newError;
      return error;
    }
  }
  return passRestriction(valueObj, metaElm, typeName);
}

function checkValidList(valueObj, metaElm, typeName, idInfo) {
  var errorStr = '';
  var list = valueObj ? valueObj.value.split(/\s+/) : [];
  for (var i=list.length-1;i>=0;i--) {
    var value = list[i];
    var valueObj = new makeValueObj(value);
    if (!value.length || value.charAt[0] == ' ') {
      list.splice(i,1);
    } else {
      var thisError;
      if (!typeName) {
        thisError = '';
      } else {
        thisError = checkValidType(valueObj, metaElm, typeName, idInfo);
      }
      if (thisError) {
        errorStr += thisError + "\n";
      } else {
          getCanonicalValue(valueObj,typeName);
        list[i] = valueObj.value;
      }
    }
  }
  if (errorStr) return errorStr;
  var constraint = metaElm ? metaElm['#lc'] : null; // get list constraint
  if (constraint) {
    try {
      var rst;
      eval('rst='+constraint);
      if (!rst) return 'list=' + list + ' does not satisify the constraint ' + singleQuote(constraint);
    } catch(err) {
      return err + ' in constraint ' + singleQuote(constraint);
    }
  }
  return list;
}

function getCanonicalValue(strObj, typeName) {
  if (strObj.canonicalized) return true;
  if (typeof(typeName) == 'object') {
    return getCanonicalValue(strObj, typeName['#dt']);
  } else if (document.schema.datatype[typeName]) { // user type
    return getCanonicalValue(strObj, document.schema.datatype[typeName]);
  }
  if (typeName.charAt(0) == '#') typeName = typeName.substr(1);
  try {
    return eval('canonical_'+typeName+'(strObj)');
  } catch(err) {
    return canonicalized(strObj);
  }
}

function checkValidListOfType(valueObj, metaElm, typeName, idInfo) {
  var rst = checkValidList(valueObj, metaElm, typeName, idInfo);
  if (typeof(rst) == 'string') return rst;
  return '';
}

function checkValidValue(valueObj, metaElm, idInfo) {
  var dt = metaElm ? metaElm['#dt'] : null;
  // TBD field with both datatype and choices
  if (dt) {
    if (typeof(dt) == 'object') {
      return checkValidValue(valueObj, dt, idInfo);
    if (error) alert(error);
    return error;
    } else if (dt.charAt(dt.length-1) == '*') {
      return checkValidListOfType(valueObj, metaElm, dt.substr(0,dt.length-1), idInfo);
    } else if (dt == '#list') {
      return checkValidListOfType(valueObj, metaElm, metaElm['#lt'], idInfo);
    } else {
    return checkValidType(valueObj, metaElm, dt, idInfo);
    }
  } else if (metaElm && metaElm['#cm']) {
    if (metaElm['#cm'].search(/<#string>/) < 0)
      return 'The content model indicates this should not be a simple value.';
  } else {
      return 'We do not recognize this element.';
  }
  return passRestriction(valueObj, metaElm, null);
}

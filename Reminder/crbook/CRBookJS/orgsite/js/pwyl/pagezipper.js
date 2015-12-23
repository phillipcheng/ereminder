function PageZipper() {
    this.nextSynonyms = [{
        syn: "next",
        weight: 100
    }, {
        syn: "older",
        weight: 80
    }, {
        syn: "previous",
        weight: 60
    }, {
        syn: "forward",
        weight: 50
    }, {
        syn: "continue",
        weight: 45
    }, {
        syn: ">",
        weight: 40,
        humanReadableOnly: true
    }, {
        syn: ">>",
        weight: 30,
        humanReadableOnly: true
    }, {
        syn: "more",
        weight: 20
    }, {
        syn: "page",
        weight: 10
    }, {
        syn: "-1",
        weight: 0,
        humanReadableOnly: true,
        pageBar: true
    }];
    this.minimumPageBuffer = 1;
    this.poster_image_margin_top = 10;
    this.poster_image_margin_bottom = 10;
    this.pages = [];
    this.is_running = false;
    this.is_loading_page = false;
    this.is_bookmarklet = false;
    this.is_extension = false;
    this.ctrl_key_pressed = false;
    this.curr_next_synonym = null;
    this.onePosterPerPageMode = false;
    this.displayMode = "text";
    this.currDomain;
    this.url_list;
    this.media_path;
    this.debug = false
}
PageZipper.prototype.loadPageZipper = function () {
    if (!window.Node) {
        window.Node = {
            ELEMENT_NODE: 1,
            TEXT_NODE: 3
        }
    }
    var currDoc = pgzp().is_extension ? document.getElementById("content").contentWindow.wrappedJSObject.document : pgzp().doc;
    currDoc.write = currDoc.writeln = currDoc.open = currDoc.close = function (str) {
        return
    };
    pgzp().currDomain = pgzp().getDomain(pgzp().win.location.href);
    pgzp().url_list = [pgzp().win.location.href];
    pgzp().addExistingPage(pgzp().doc.body, pgzp().win.location.href);
    pgzp().displayMode = pgzp().calculateDisplayMode(pgzp().pages[0]);
    if (pgzp().displayMode == "image" && pgzp().pages[0].posterImgs.length == 1) {
        pgzp().onePosterPerPageMode = true
    }
    if (pgzp().currDomain == "boston.com") {
        pgzp().poster_image_margin_bottom = 60
    }
    if (pgzp().currDomain == "nytimes.com") {
        pgzp().poster_image_margin_top = 40
    }
};
PageZipper.prototype.runPageZipper = function () {
    pgzp().addEventListener(pgzp().doc, "keydown", this.keyDown);
    pgzp().addEventListener(pgzp().doc, "keyup", this.keyUp);
    pgzp().addEventListener(pgzp().win, "resize", this.positionMenu);
    pgzp().addMenu();
    this.is_running = pgzp().win.setInterval(pgzp().mainBlock, 250)
};
PageZipper.prototype.stopPageZipper = function () {
    if (this.is_running) {
        pgzp().win.clearInterval(this.is_running);
        this.is_running = null;
        pgzp().removeMenu();
        pgzp().removeEventListener(pgzp().doc, "keydown", this.keyDown);
        pgzp().removeEventListener(pgzp().doc, "keyup", this.keyUp);
        pgzp().removeEventListener(pgzp().win, "resize", this.positionMenu)
    }
};
PageZipper.prototype.mainBlock = function () {
    if (!pgzp()) {
        return
    }
    var currPageIndex = pgzp().getCurrentPage();
    var currViewablePage = pgzp().getViewableCurrentPage(currPageIndex);
    pgzp().menuSetCurrPageNumber(currViewablePage + 1);
    if (!pgzp().is_loading_page && pgzp().pages[pgzp().pages.length - 1]["nextLink"] && ((pgzp().pages.length - currPageIndex - 1) < pgzp().minimumPageBuffer || (pgzp().onePosterPerPageMode && ((pgzp().pages.length - currPageIndex) < 4)))) {
        pgzp().is_loading_page = true;
        pgzp().url_list.push(pgzp().pages[pgzp().pages.length - 1].nextLink.url);
        pgzp().loadPage(pgzp().pages[pgzp().pages.length - 1].nextLink.url)
    }
};
PageZipper.prototype.getCurrentPage = function () {
    var i, currPage, currPagePos, currPageTop, currPageBottom;
    var currViewBottom = pgzp().screen.getScrollTop() + pgzp().screen.getViewportHeight();
    for (i = 0; i < pgzp().pages.length; i++) {
        currPage = pgzp().pages[i].page;
        currPagePos = pgzp().findPos(currPage);
        currPageTop = currPagePos.y;
        if (i == (pgzp().pages.length - 1)) {
            currPageBottom = pgzp().screen.getDocumentHeight()
        } else {
            currPageBottom = pgzp().findPos(pgzp().pages[(i + 1)].page).y
        }
        if (i == (pgzp().pages.length - 1) && currPageBottom < currViewBottom) {
            currPageBottom = currViewBottom
        }
        if (currPageTop <= currViewBottom && currViewBottom <= currPageBottom) {
            return i
        }
    }
    return pgzp().pages.length + 1
};
PageZipper.prototype.getViewableCurrentPage = function (currPage) {
    var currPageObj = pgzp().pages[currPage];
    if ((pgzp().findPos(currPageObj.page).y - Math.abs(pgzp().screen.getScrollTop())) > (pgzp().screen.getViewportHeight() / 2)) {
        return currPage - 1
    }
    return currPage
};
PageZipper.prototype.loadPage = function (url) {
    pgzp().jx.load(url, function (data) {
        var results = data.match(/<body.*?>([\w\W]*?)<\/body>/i);
        data = (results && results.length >= 2) ? results[1] : data;
        pgzp().processPageAdd(data, url)
    })
};
PageZipper.prototype.processPageAdd = function (nextPageData, url) {
    var nextPage = pgzp().buildPageFromData(nextPageData, url);
    pgzp().pages.push(nextPage);
    pgzp().copyPage(nextPage.page);
    pgzp().removeAbsolutePositioning(nextPage.page);
    pgzp().menuIncrementPagesLoaded();
    nextPage.nextLink = pgzp().getNextLink(nextPage.page);
    pgzp().is_loading_page = false;
    pgzp().mainBlock()
};
PageZipper.prototype.addExistingPage = function (body, url) {
    var nextPage = pgzp().buildPage(body, url);
    pgzp().pages.push(nextPage);
    pgzp().removeAbsolutePositioning(nextPage.page);
    nextPage.posterImgs = pgzp().getPosterImagesOnPage(nextPage.page);
    nextPage.nextLink = pgzp().getNextLink(nextPage.page)
};
PageZipper.prototype.buildPageFromData = function (data, url) {
    var page = pgzp().doc.createElement("div");
    page.id = "pgzp_page" + pgzp().pages.length;
    page.style.clear = "both";
    page.innerHTML = data;
    return pgzp().buildPage(page, url)
};
PageZipper.prototype.buildPage = function (page, url) {
    return {
        page: page,
        nextLink: null,
        posterImgs: null,
        url: url
    }
};
PageZipper.prototype.copyPage = function (body) {
    pgzp().doc.body.appendChild(body)
};
PageZipper.prototype.removeAbsolutePositioning = function (body) {
    for (var i = 0; i < body.childNodes.length; i++) {
        if (body.childNodes[i].nodeType == 1 && pgzp().css.getStyle(body.childNodes[i], "position") == "absolute") {
            pgzp().css.setStyle(body.childNodes[i], "position", "static")
        }
    }
};
PageZipper.prototype.calculateDisplayMode = function (currPage) {
    var textArea = 0,
        imgArea = 0;
    var i = 0,
        txtP, imgs = {};
    txtP = pgzp().getAllTextOnPage(currPage.page);
    pgzp().doc.body.appendChild(txtP);
    textArea = txtP.offsetWidth * txtP.offsetHeight;
    pgzp().doc.body.removeChild(txtP);
    if (currPage.posterImgs == null) {
        currPage.posterImgs = pgzp().getPosterImagesOnPage(currPage.page)
    }
    for (i = 0; i < currPage.posterImgs.length; i++) {
        imgs[currPage.posterImgs[i].src] = currPage.posterImgs[i]
    }
    for (imgUrl in imgs) {
        var img = imgs[imgUrl];
        imgArea += img.offsetHeight * img.offsetWidth
    }
    return (textArea >= imgArea) ? "text" : "image"
};
PageZipper.prototype.getAllTextOnPage = function (pageHtml) {
    var str = "";
    pgzp().depthFirstRecursion(pageHtml, function (curr) {
        if (curr.nodeType == 3 && curr.parentNode.nodeType == 1) {
            var tagName = curr.parentNode.tagName.toLowerCase();
            if (tagName == "div" || tagName == "span" || tagName == "p" || tagName == "td") {
                str += curr.nodeValue + "\n"
            }
        }
    });
    var p = pgzp().doc.createElement("p");
    p.appendChild(pgzp().doc.createTextNode(str));
    return p
};
PageZipper.prototype.goToNext = function (inc) {
    var currPageIndex = pgzp().getViewableCurrentPage(pgzp().getCurrentPage());
    if (pgzp().displayMode == "text") {
        pgzp().goToNextPage(inc, currPageIndex)
    } else {
        if (inc > 0) {
            pgzp().goToNextPosterImage()
        } else {
            pgzp().goToPreviousPosterImage()
        }
    }
};
PageZipper.prototype.nextArrow = function () {
    pgzp().goToNext(1)
};
PageZipper.prototype.prevArrow = function () {
    pgzp().goToNext(-1)
};
PageZipper.prototype.goToNextPage = function (inc, currPageIndex) {
    var currPage, pos, amountToScroll, ps;
    currPageIndex += inc;
    if (currPageIndex in pgzp().pages) {
        currPage = pgzp().pages[currPageIndex].page;
        amountToScroll = pgzp().findPos(currPage).y - pgzp().screen.getScrollTop();
        pgzp().win.scrollBy(0, amountToScroll)
    }
};
PageZipper.prototype.getPosterImagesOnPage = function (page) {
    var posterImgs = [],
        filteredImages = [];
    var okImgDomains = {
        "www.flickr.com": 1
    };
    var isFillerImg = function (img) {
        if ((img.offsetWidth * img.offsetHeight) < (100 * 100)) {
            filteredImages.push(img);
            return true
        }
        var p = img.parentNode;
        if (p.nodeType == Node.ELEMENT_NODE && p.tagName.toLowerCase() == "a") {
            if (pgzp().getDomain(p.href) != pgzp().currDomain && okImgDomains[pgzp().getDomain(p.href)] != 1) {
                return true
            }
        }
        return false
    };
    var getBiggestImg = function (imgs) {
        var biggestImg = null;
        for (var i = 0; i < imgs.length; i++) {
            if (biggestImg == null || ((imgs[i].offsetWidth * imgs[i].offsetHeight) > (biggestImg.offsetWidth * biggestImg.offsetHeight))) {
                biggestImg = imgs[i]
            }
        }
        return biggestImg
    };
    var imgs = pgzp().convertToArray(page.getElementsByTagName("img"));
    pgzp().filter(imgs, isFillerImg);
    if (imgs.length < 2) {
        return imgs
    }
    imgs.sort(function (a, b) {
        var sizeA = a.offsetWidth * a.offsetHeight;
        var sizeB = b.offsetWidth * b.offsetHeight;
        return sizeB - sizeA
    });
    if (pgzp().onePosterPerPageMode) {
        return [imgs[0]]
    }
    var biggestSmallImg = getBiggestImg(filteredImages);
    if (biggestSmallImg) {
        imgs.push(biggestSmallImg)
    }
    var biggestGap = [0, 1];
    for (var i = 1; i < imgs.length; i++) {
        var bigger = imgs[i - 1],
            biggerSize = bigger.offsetHeight * bigger.offsetWidth;
        var smaller = imgs[i],
            smallerSize = smaller.offsetHeight * smaller.offsetWidth;
        var relGap = (biggerSize == 0 || smallerSize == 0 ? 0 : (biggerSize / smallerSize)),
            absGap = (biggerSize - smallerSize),
            totalGap = (relGap * absGap);
        if (totalGap >= biggestGap[0]) {
            biggestGap = [totalGap, i]
        }
    }
    imgs.splice(biggestGap[1], (imgs.length - biggestGap[1]));
    imgs.sort(function (a, b) {
        return pgzp().findPos(a).y - pgzp().findPos(b).y
    });
    return imgs
};
PageZipper.prototype.resizeImageToViewport = function (img) {
    var usableViewport = pgzp().screen.getViewportHeight() - pgzp().poster_image_margin_top - pgzp().poster_image_margin_bottom;
    if (img.offsetHeight > usableViewport) {
        img.style.width = (usableViewport / img.offsetHeight) * img.offsetWidth + "px";
        img.style.height = usableViewport + "px"
    }
};
PageZipper.prototype.goToNextPosterImage = function () {
    var browserBorderTop = pgzp().screen.getScrollTop() + pgzp().poster_image_margin_top + 1;
    for (var i = 0; i < pgzp().pages.length; i++) {
        if (pgzp().pages[i].posterImgs == null) {
            pgzp().pages[i].posterImgs = pgzp().getPosterImagesOnPage(pgzp().pages[i].page)
        }
        for (var j = 0; j < pgzp().pages[i].posterImgs.length; j++) {
            var currPosterImg = pgzp().pages[i].posterImgs[j];
            var pos = pgzp().findPos(currPosterImg);
            if (pos.y > browserBorderTop) {
                pgzp().resizeImageToViewport(currPosterImg);
                var amountToScroll = (pos.y - pgzp().poster_image_margin_top) - pgzp().screen.getScrollTop();
                pgzp().win.scrollBy(0, amountToScroll);
                return
            }
        }
    }
};
PageZipper.prototype.goToPreviousPosterImage = function () {
    var browserBorderTop = pgzp().screen.getScrollTop() + pgzp().poster_image_margin_top - 1;
    for (var i = (pgzp().pages.length - 1); i >= 0; i--) {
        if (pgzp().pages[i].posterImgs == null) {
            pgzp().pages[i].posterImgs = pgzp().getPosterImagesOnPage(pgzp().pages[i].page)
        }
        for (var j = (pgzp().pages[i].posterImgs.length - 1); j >= 0; j--) {
            var currPosterImg = pgzp().pages[i].posterImgs[j];
            var pos = pgzp().findPos(currPosterImg);
            if (pos.y < browserBorderTop) {
                pgzp().resizeImageToViewport(currPosterImg);
                var amountToScroll = (pos.y - pgzp().poster_image_margin_top) - pgzp().screen.getScrollTop();
                pgzp().win.scrollBy(0, amountToScroll);
                return
            }
        }
    }
};
PageZipper.prototype.keyDown = function (event) {
    event = pgzp().captureEvent(event);
    switch (event.keyCode) {
        case 40:
        case 190:
            if (pgzp().ctrl_key_pressed) {
                pgzp().goToNext(1);
                pgzp().noBubble(event)
            }
            break;
        case 38:
        case 188:
            if (pgzp().ctrl_key_pressed) {
                pgzp().goToNext(-1);
                pgzp().noBubble(event)
            }
            break;
        case 17:
        case 224:
            pgzp().ctrl_key_pressed = true;
            break
    }
};
PageZipper.prototype.keyUp = function (event) {
    event = pgzp().captureEvent(event);
    switch (event.keyCode) {
        case 17:
        case 224:
            pgzp().ctrl_key_pressed = false;
            break
    }
};
PageZipper.prototype.addMenu = function () {
    var css = "																																								#pgzp_menu a, #pgzp_menu a * {border: 0; text-decoration: none;}																										#pgzp_menu {position: fixed; top: 0px; float:left; padding: 0px 5px; background-color: #D3D3D3; color: black; z-index: 10000;}														.pgzp_block {display: block; float: left;}																																.pgzp_button {display: block; width: 32px; height: 32px;}																												a.pgzp_button_prev_active {background: transparent url('${media_path}32-gnome-prev.png') no-repeat scroll top left; }					a:hover.pgzp_button_prev_active {background-image: url('${media_path}32-gnome-prev_red.png'); }											a.pgzp_button_prev_inactive {background: transparent url('${media_path}32-gnome-prev_gray.png') no-repeat scroll top left; }			a.pgzp_button_next_active {background: transparent url('${media_path}32-gnome-next.png') no-repeat scroll top left; }					a:hover.pgzp_button_next_active {background-image: url('${media_path}32-gnome-next_red.png'); }											a.pgzp_button_next_inactive {background: transparent url('${media_path}32-gnome-next_gray.png') no-repeat scroll top left; }			#pgzp_curr_page {font-size: 24px;}																																		#pgzp_loaded_pages {font-size: 18px;}																																";
    var html = "																																									<div id='pgzp_menu'>																																							<a href='javascript:pgzp().goToNext(-1)' id='pgzp_button_prev' class='pgzp_block pgzp_button pgzp_button_prev_active' title='Previous - Cntrl Up or Cntrl <'></a>				<a href='javascript:pgzp().goToNext(1)' id='pgzp_button_next' class='pgzp_block pgzp_button pgzp_button_next_active' title='Next - Cntrl Down or Cntrl >'></a>				<div class='pgzp_block' style='padding-left: 5px;'><span id='pgzp_curr_page' title='Current Page'>1</span><span id='pgzp_loaded_pages' title='Pages Loaded'>/1</span></div>											<a href='http://www.printwhatyoulike.com/pagezipper' target='_blank' title='PageZipper Home' class='pgzp_block pgzp_button' style='padding-left: 5px'>																<img src='${media_path}zipper_32.png' alt='PageZipper!' style='border-width: 0px' />													</a>																																									</div>																																									";
    css = pgzp().strip(css.replace(/\$\{media_path\}/g, pgzp().media_path));
    html = pgzp().strip(html.replace(/\$\{media_path\}/g, pgzp().media_path));
    var cssElem = pgzp().doc.createElement("style");
    cssElem.setAttribute("type", "text/css");
    if (cssElem.styleSheet) {
        cssElem.styleSheet.cssText = css
    } else {
        cssElem.appendChild(pgzp().doc.createTextNode(css))
    }
    pgzp().doc.getElementsByTagName("head")[0].appendChild(cssElem);
    var div = pgzp().doc.createElement("div");
    div.innerHTML = html;
    div = div.childNodes[0];
    pgzp().doc.body.appendChild(div);
    pgzp().positionMenu();
    if (pgzp().is_extension) {
        var fixLink = function (linkId, eventHandler) {
            var link = pgzp().doc.getElementById(linkId);
            link.removeAttribute("href");
            pgzp().addEventListener(link, "click", eventHandler)
        };
        fixLink("pgzp_button_prev", pgzp().prevArrow);
        fixLink("pgzp_button_next", pgzp().nextArrow)
    }
};
PageZipper.prototype.positionMenu = function () {
    var div = pgzp().doc.getElementById("pgzp_menu");
    div.style.left = (pgzp().screen.getViewportWidth() - div.offsetWidth - 30) + "px"
};
PageZipper.prototype.removeMenu = function () {
    var menu = pgzp().doc.getElementById("pgzp_menu");
    if (menu) {
        pgzp().doc.body.removeChild(menu)
    }
};
PageZipper.prototype.menuIncrementPagesLoaded = function () {
    var loadedPages = pgzp().doc.getElementById("pgzp_loaded_pages"),
        num;
    if (loadedPages) {
        num = parseInt(loadedPages.innerHTML.replace("/", "", "g"), 10);
        num++;
        loadedPages.innerHTML = "/" + num
    }
};
PageZipper.prototype.menuSetCurrPageNumber = function (currPage) {
    var currPageObj = pgzp().pages[currPage - 1];
    pgzp().doc.getElementById("pgzp_curr_page").innerHTML = currPage;
    if (pgzp().displayMode == "text") {
        pgzp().updateButtonState((currPage != 1), "prev");
        pgzp().updateButtonState((currPage != pgzp().pages.length), "next")
    } else {
        var top = pgzp().screen.getScrollTop();
        var displayPrev = (pgzp().findPos(pgzp().pages[0].posterImgs[0]).y < top);
        pgzp().updateButtonState(displayPrev, "prev");
        var disableNext = (currPage == pgzp().pages.length && currPageObj.posterImgs && pgzp().findPos(currPageObj.posterImgs[currPageObj.posterImgs.length - 1]).y < (top + pgzp().poster_image_margin_top + 1));
        pgzp().updateButtonState(!disableNext, "next")
    }
};
PageZipper.prototype.updateButtonState = function (enable, buttonName) {
    var button = pgzp().doc.getElementById("pgzp_button_" + buttonName);
    var activeClass = "pgzp_button_" + buttonName + "_active";
    var inactiveClass = "pgzp_button_" + buttonName + "_inactive";
    if (enable) {
        pgzp().css.replaceClass(button, inactiveClass, activeClass)
    } else {
        pgzp().css.replaceClass(button, activeClass, inactiveClass)
    }
};
PageZipper.prototype.NextLink = function (text, link, alreadyLoaded, isHumanReadableText) {
    this.text = text;
    this.link = link;
    this.syn = "";
    this.isHumanReadableText = (isHumanReadableText == undefined) ? true : isHumanReadableText;
    this.isVisibleText = false;
    this.alreadyLoaded = alreadyLoaded;
    this.url = link.href;
    this.finalScore = null;
    this.trialScores = {};
    this.addScore = function (trialName, score, isNormalized) {
        var normalizedKey = isNormalized ? "normalizedScore" : "unnormalizedScore";
        if (!this.trialScores[trialName]) {
            this.trialScores[trialName] = {}
        }
        this.trialScores[trialName][normalizedKey] = score
    };
    this.getScore = function (trialName, isNormalized) {
        if (isNormalized && pgzp().trials[trialName].noNormailization) {
            isNormalized = false
        }
        var normalizedKey = isNormalized ? "normalizedScore" : "unnormalizedScore";
        return this.trialScores[trialName][normalizedKey]
    };
    this.calculateTotalScore = function () {
        this.finalScore = 0;
        if (pgzp().debug) {
            var debugStr = "Calculate Final Score: " + this.text + ": " + this.url
        }
        for (var trial in this.trialScores) {
            if (pgzp().trials.hasOwnProperty(trial)) {
                var nScore = this.getScore(trial, true);
                var weight = pgzp().trials[trial].weight;
                this.finalScore += nScore * weight;
                if (pgzp().debug) {
                    debugStr += "\n\t" + trial + "\t\t\t" + nScore + "\tx\t" + weight + "\t=\t" + (nScore * weight)
                }
            }
        }
        if (pgzp().debug) {
            pgzp().log(debugStr + "\nFinal Score:\t" + this.finalScore)
        }
        return this.finalScore
    };
    this.isSynNumber = function () {
        return pgzp().isNumber(this.syn)
    }
};
PageZipper.prototype.trials = {
    contains_next_syn: {
        doScore: function (nextLink) {
            var i, currWord, score = 0;
            for (i = 0; i < pgzp().nextSynonyms.length; i++) {
                currWord = pgzp().nextSynonyms[i];
                if (nextLink.text.toLowerCase().indexOf(currWord.syn) >= 0) {
                    if (currWord.humanReadableOnly) {
                        if (!nextLink.isHumanReadableText || nextLink.text.toLowerCase().indexOf("comment") >= 0) {
                            continue
                        }
                    }
                    if (currWord.syn != "next" && !pgzp().isStandaloneWord(currWord.syn, nextLink.text, nextLink.isHumanReadableText)) {
                        continue
                    }
                    if (currWord.pageBar && !nextLink.isPageBar) {
                        continue
                    }
                    if (currWord.weight >= score) {
                        score = currWord.weight;
                        nextLink.syn = currWord.syn
                    }
                } else {
                    if (!currWord.humanReadableOnly && nextLink.url.toLowerCase().indexOf(currWord.syn) >= 0) {
                        if (!pgzp().isStandaloneWord(currWord.syn, nextLink.url, false)) {
                            continue
                        }
                        if (currWord.weight >= score) {
                            score = currWord.weight;
                            nextLink.syn = currWord.syn
                        }
                    }
                }
            }
            return score
        },
        weight: 100,
        noNormailization: true
    },
    url_similarity: {
        doScore: function (nextLink) {
            var lastUrl, currUrl, shorter, longer, score = 0,
                notMatchingPos = -1,
                i;
            lastUrl = pgzp().pages[pgzp().pages.length - 1].url;
            currUrl = nextLink.url;
            if (lastUrl.length <= currUrl.length) {
                shorter = lastUrl;
                longer = currUrl
            } else {
                shorter = currUrl;
                longer = lastUrl
            }
            score = shorter.length - longer.length;
            for (i = 0; i < shorter.length; i++) {
                if (shorter.charAt(i) == longer.charAt(i)) {
                    score++
                } else {
                    score--;
                    if (notMatchingPos < 0) {
                        notMatchingPos = i
                    }
                }
            }
            if (notMatchingPos > 0 && pgzp().isNumber(longer.charAt(notMatchingPos)) && pgzp().isNumber(shorter.charAt(notMatchingPos)) && (Math.abs(pgzp().getNumberAtPos(shorter, notMatchingPos) - pgzp().getNumberAtPos(longer, notMatchingPos)) == 1)) {
                score += 100
            }
            return score
        },
        weight: 70
    },
    duplicate_text: {
        doScore: function (nextLink) {
            var score = 100;
            if (pgzp().linkTextIndex[nextLink.text] && pgzp().linkTextIndex[nextLink.text].length > 0) {
                score = score - (pgzp().linkTextIndex[nextLink.text].length - 1) * 20
            }
            return score
        },
        weight: 60
    },
    url_ends_in_number: {
        doScore: function (nextLink) {
            var results = nextLink.url.match(/^.*?(\d+)(\/+|\.\w+)?$/);
            if (results && (parseInt(results[1], 10) < 99)) {
                return 100
            } else {
                return 0
            }
        },
        weight: 20
    },
    begins_or_ends_with_next_syn: {
        doScore: function (nextLink) {
            if (nextLink.syn && (pgzp().startsWith(nextLink.syn, nextLink.text.toLowerCase()) || pgzp().endsWith(nextLink.syn, nextLink.text.toLowerCase()))) {
                return 100
            } else {
                return 0
            }
        },
        weight: 20
    },
    text_size: {
        doScore: function (nextLink) {
            return Math.floor((nextLink.link.offsetWidth * nextLink.link.offsetHeight) / nextLink.text.length)
        },
        weight: 10
    },
    chars_in_text: {
        doScore: function (nextLink) {
            return nextLink.text.length * -1
        },
        weight: 10
    }
};
PageZipper.prototype.getNextLink = function (body) {
    var allNextLinks = pgzp().getAllNextLinks(body);
    var pageBarInfo = pgzp().getCurrentPageNumberFromPageBar(allNextLinks);
    pgzp().log("looking for page #: " + (pageBarInfo[0] + 1) + " w/confidence: " + pageBarInfo[1]);
    pgzp().nextSynonyms[pgzp().nextSynonyms.length - 1].syn = (pageBarInfo[0] + 1) + "";
    pgzp().nextSynonyms[pgzp().nextSynonyms.length - 1].weight = pageBarInfo[1];
    pgzp().linkTextIndex = pgzp().indexDuplicateLinks(allNextLinks);
    pgzp().filter(allNextLinks, function (link) {
        return link.alreadyLoaded
    });
    pgzp().scoreLinks(allNextLinks);
    pgzp().normalizeLinks(allNextLinks);
    if (allNextLinks.length <= 0) {
        return null
    }
    var highestLink = pgzp().getHighestTotalScore(allNextLinks);
    if (pgzp().pages.length > 1 && !pgzp().pages[0].nextLink.isSynNumber() && !highestLink.isSynNumber() && pgzp().pages[0].nextLink.syn != highestLink.syn) {
        return null
    }
    return highestLink
};
PageZipper.prototype.scoreLinks = function (allNextLinks) {
    if (pgzp().debug) {
        var debugMsg = ""
    }
    for (var trial in pgzp().trials) {
        if (pgzp().trials.hasOwnProperty(trial)) {
            for (var i = 0; i < allNextLinks.length; i++) {
                allNextLinks[i].addScore(trial, pgzp().trials[trial].doScore(allNextLinks[i]));
                if (pgzp().debug) {
                    debugMsg += "\nScore " + trial + " " + allNextLinks[i].text + ": " + allNextLinks[i].getScore(trial)
                }
                if (trial == "contains_next_syn" && allNextLinks[i].getScore("contains_next_syn") <= 0) {
                    allNextLinks.splice(i, 1);
                    i--
                }
            }
        }
    }
    if (pgzp().debug) {
        pgzp().log(debugMsg)
    }
};
PageZipper.prototype.normalizeLinks = function (allLinks) {
    for (var trial in pgzp().trials) {
        if (pgzp().trials.hasOwnProperty(trial) && !pgzp().trials[trial].noNormailization) {
            pgzp().normalizeTrialSet(trial, allLinks)
        }
    }
};
PageZipper.prototype.normalizeTrialSet = function (trialName, allLinks) {
    var highest, lowest = null;
    for (var i = 0; i < allLinks.length; i++) {
        var score = allLinks[i].getScore(trialName);
        if (highest == null || score > highest) {
            highest = score
        }
        if (lowest == null || score < lowest) {
            lowest = score
        }
    }
    if (pgzp().debug) {
        var debugMsg = "Normalizing Trial Set: " + trialName
    }
    var curve = (highest == lowest) ? 0 : (100 / (highest - lowest));
    for (var i = 0; i < allLinks.length; i++) {
        var score = allLinks[i].getScore(trialName);
        var nScore = Math.floor((score - lowest) * curve);
        allLinks[i].addScore(trialName, nScore, true);
        if (pgzp().debug) {
            debugMsg += "\nNScore " + i + ": " + allLinks[i].text + ": score: " + score + " curve: " + curve + " higest: " + highest + " lowest: " + lowest + " nscore: " + nScore
        }
    }
    if (pgzp().debug) {
        pgzp().log(debugMsg)
    }
};
PageZipper.prototype.getHighestTotalScore = function (allNextLinks) {
    var highestScoringLink = null;
    for (var i = 0; i < allNextLinks.length; i++) {
        var score = allNextLinks[i].calculateTotalScore();
        if (highestScoringLink == null || score >= highestScoringLink.finalScore) {
            highestScoringLink = allNextLinks[i]
        }
    }
    if (pgzp().debug) {
        var debugMsg = "Final Score " + allNextLinks.length;
        allNextLinks.sort(function (a, b) {
            return b.finalScore - a.finalScore
        });
        for (i = 0; i < allNextLinks.length; i++) {
            debugMsg += "\n" + allNextLinks[i].finalScore + ": " + allNextLinks[i].text + ": " + allNextLinks[i].url
        }
        pgzp().log(debugMsg)
    }
    return highestScoringLink
};
PageZipper.prototype.getAllNextLinks = function (body) {
    var allNextLinks = [];
    var links = body.getElementsByTagName("a");
    var pageUrl = pgzp().getUrlWOutAnchors(pgzp().pages[pgzp().pages.length - 1].url);
    for (var i = 0; i < links.length; i++) {
        if (pgzp().getDomain(links[i].href) == pgzp().currDomain && (links[i].href.indexOf("#") < 0 || pageUrl != pgzp().getUrlWOutAnchors(links[i].href))) {
            pgzp().addLinkComponents(links[i], allNextLinks, pgzp().contains(pgzp().url_list, links[i].href))
        }
    }
    return allNextLinks
};
PageZipper.prototype.addLinkComponents = function (link, allNextLinks, alreadyLoaded) {
    var NextLink = pgzp().NextLink;
    var search = function (rootNode) {
        for (var i = 0; i < rootNode.childNodes.length; i++) {
            var curr = rootNode.childNodes[i];
            if (curr.nodeType == Node.TEXT_NODE && curr.nodeValue && pgzp().strip(curr.nodeValue).length > 0) {
                var nl = new NextLink(curr.nodeValue, link, alreadyLoaded);
                nl.isVisibleText = true;
                allNextLinks.push(nl)
            } else {
                if (curr.nodeType == Node.ELEMENT_NODE && curr.tagName.toLowerCase() == "img") {
                    if (curr.alt) {
                        allNextLinks.push(new NextLink(curr.alt, link, alreadyLoaded))
                    }
                    if (curr.title) {
                        allNextLinks.push(new NextLink(curr.title, link, alreadyLoaded))
                    }
                    if (curr.src) {
                        allNextLinks.push(new NextLink(curr.src, link, alreadyLoaded, false))
                    }
                } else {
                    search(curr)
                }
            }
        }
    };
    if (link.title) {
        allNextLinks.push(new NextLink(link.title, link))
    }
    if (link.alt) {
        allNextLinks.push(new NextLink(link.alt, link))
    }
    search(link)
};
PageZipper.prototype.getCurrentPageNumberFromPageBar = function (allNextLinks) {
    var allSequences = [],
        i = 0,
        currSeq = [],
        currNextLink, pageBar, pageBarScore = 0,
        pageNum, tmpPageBarScore;
    var currPageUrl = pgzp().pages[pgzp().pages.length - 1].url;
    for (i = 0; i < allNextLinks.length; i++) {
        currNextLink = allNextLinks[i];
        if (currNextLink.isVisibleText) {
            if (pgzp().isNumber(currNextLink.text)) {
                pageNum = parseInt(currNextLink.text, 10);
                if (pageNum >= 0 && pageNum < 1000) {
                    currNextLink.pageNum = pageNum;
                    currSeq.push(currNextLink)
                }
            } else {
                if (currSeq.length > 0) {
                    allSequences.push(currSeq);
                    currSeq = []
                }
            }
        }
    }
    if (currSeq.length > 0) {
        allSequences.push(currSeq)
    }
    for (i = 0; i < allSequences.length; i++) {
        tmpPageBarScore = pgzp().__scorePageBar(allSequences[i]);
        if (tmpPageBarScore >= pageBarScore) {
            pageBarScore = tmpPageBarScore;
            pageBar = allSequences[i]
        }
    }
    if (!pageBar) {
        return [-1, 0]
    }
    pageBar.sort(function (a, b) {
        return a.pageNum - b.pageNum
    });
    pgzp().logList(pageBar, "indexes ordered by size", "#{o.pageNum}\t#{o.text}");
    for (i = 0; i < pageBar.length; i++) {
        pageBar[i].isPageBar = true
    }
    for (i = 0; i < pageBar.length; i++) {
        if (pageBar[i].url == currPageUrl) {
            return [pageBar[i].pageNum, 120]
        }
    }
    if (pageBar.length >= 2) {
        var currPageNum, prevPageNum = pageBar[0].pageNum;
        for (i = 1; i < pageBar.length; i++) {
            currPageNum = pageBar[i].pageNum;
            if (Math.abs(currPageNum - prevPageNum) == 2) {
                return [currPageNum - 1, 120]
            } else {
                prevPageNum = currPageNum
            }
        }
    }
    if (pageBar[0].pageNum == 2) {
        return [1, 30]
    }
    if (pageBar[0].pageNum == 1) {
        return [0, 30]
    }
    return [pageBar[pageBar.length - 1].pageNum, 20]
};
PageZipper.prototype.__scorePageBar = function (pageBar) {
    var similarityScore = pgzp().trials.url_similarity.doScore(pageBar[0]);
    var totalScore = pageBar.length + (similarityScore / 20);
    pgzp().log("page bar length: " + pageBar.length + " sim score: " + similarityScore + " total score: " + totalScore);
    return totalScore
};
PageZipper.prototype.indexDuplicateLinks = function (allNextLinks) {
    var textIndex = {};
    var currLink;
    for (var i = 0; i < allNextLinks.length; i++) {
        currLink = allNextLinks[i];
        if (textIndex[currLink.text]) {
            if (!pgzp().contains(textIndex[currLink.text], currLink.url)) {
                textIndex[currLink.text].push(currLink.url)
            }
        } else {
            textIndex[currLink.text] = [currLink.url]
        }
    }
    return textIndex
};
PageZipper.prototype.strip = function (str) {
    return str.replace(/^\s+/, "").replace(/\s+$/, "")
};
PageZipper.prototype.startsWith = function (pattern, str) {
    return str.indexOf(pattern) === 0
};
PageZipper.prototype.endsWith = function (pattern, str) {
    var d = str.length - pattern.length;
    return d >= 0 && str.lastIndexOf(pattern) === d
};
PageZipper.prototype.log = function (html, override) {
    if (pgzp().debug || override) {
        if (pgzp().win.console) {
            pgzp().win.console.log(html);
            return
        }
        var div = pgzp().doc.createElement("textarea");
        pgzp().doc.body.appendChild(div);
        div.value = html
    }
};
PageZipper.prototype.logList = function (list, initialStr, listStr) {
    var interpolate = function (s, o) {
        return s.replace(/\#\{([^}]+)\}/g, function (match, exp) {
            return eval(exp)
        })
    };
    for (var i = 0; i < list.length; i++) {
        initialStr += "\n" + interpolate(listStr, list[i])
    }
    pgzp().log(initialStr)
};
PageZipper.prototype.captureEvent = function (event) {
    if (!event) {
        event = pgzp().win.event
    }
    return event
};
PageZipper.prototype.noBubble = function (event) {
    if (event) {
        event.cancelBubble = true;
        if (event.stopPropagation) {
            event.stopPropagation()
        }
        event.returnValue = false
    }
    return event
};
PageZipper.prototype.getRemaningBufferSize = function () {
    var left = pgzp().screen.getDocumentHeight() - pgzp().screen.getScrollTop() - pgzp().screen.getViewportHeight();
    if (left < 0) {
        return 0
    }
    return Math.floor(left)
};
PageZipper.prototype.findPos = function (obj) {
    var curleft = 0,
        curtop = 0;
    if (obj.offsetParent) {
        do {
            curleft += obj.offsetLeft;
            curtop += obj.offsetTop
        } while (obj = obj.offsetParent)
    }
    return {
        x: curleft,
        y: curtop
    }
};
PageZipper.prototype.isNumber = function (str) {
    return str && (typeof (str) == "number" || (typeof (str) == "string" && (str.search(/^\d+$/) >= 0)))
};
PageZipper.prototype.getDomain = function (url) {
    var hna = url.match(/^http\:\/\/([\S]+?\.\w+)(\/.*)?$/i);
    if (hna) {
        var parts = hna[1].split(".");
        if (parts.length > 2) {
            return parts[parts.length - 2] + "." + parts[parts.length - 1]
        }
        return hna[1]
    }
    hna = url.match(/^javascript\:.*$/i);
    if (hna) {
        return null
    }
    return pgzp().currDomain
};
PageZipper.prototype.getUrlWOutAnchors = function (url) {
    if (url.indexOf("#") >= 0) {
        var results = url.match(/(.*?)#.*/);
        if (results.length > 0) {
            return results[1]
        }
    }
    return url
};
PageZipper.prototype.convertToArray = function (a) {
    var b = [];
    for (var i = 0; i < a.length; i++) {
        b.push(a[i])
    }
    return b
};
PageZipper.prototype.filter = function (arr, filter) {
    for (var i = 0; i < arr.length; i++) {
        if (filter(arr[i])) {
            arr.splice(i, 1);
            i--
        }
    }
};
PageZipper.prototype.depthFirstRecursion = function (root, callback) {
    for (var i = 0; i < root.childNodes.length; i++) {
        if (root.childNodes[i].nodeType == 3 || (root.childNodes[i].nodeType == 1 && pgzp().css.getStyle(root.childNodes[i], "display") != "none")) {
            pgzp().depthFirstRecursion(root.childNodes[i], callback)
        }
    }
    callback(root)
};
PageZipper.prototype.addEventListener = function (obj, type, callback) {
    if (document.addEventListener) {
        obj.addEventListener(type, callback, false)
    } else {
        obj.attachEvent("on" + type, callback)
    }
};
PageZipper.prototype.removeEventListener = function (obj, type, callback) {
    if (document.removeEventListener) {
        obj.removeEventListener(type, callback, false)
    } else {
        obj.detachEvent("on" + type, callback)
    }
};
PageZipper.prototype.contains = function (ar, obj) {
    if (Array.indexOf) {
        return ar.indexOf(obj) != -1
    } else {
        for (var i = 0; i < ar.length; i++) {
            if (ar[i] == obj) {
                return true
            }
        }
        return false
    }
};
PageZipper.prototype.getContentType = function () {
    var metas = pgzp().doc.getElementsByTagName("head")[0].getElementsByTagName("meta");
    for (var i = 0; i < metas.length; i++) {
        if (metas[i].getAttribute("http-equiv") && metas[i].getAttribute("http-equiv").toLowerCase() == "content-type" && metas[i].getAttribute("content")) {
            return metas[i].getAttribute("content")
        }
    }
    return null
};
PageZipper.prototype.isStandaloneWord = function (word, text, humanReadable) {
    var delimiter = humanReadable ? "\\s" : "[^a-zA-Z]";
    return new RegExp("^(.*" + delimiter + "+)?" + word + "(" + delimiter + "+.*)?$", "i").test(text)
};
PageZipper.prototype.getNumberAtPos = function (str, pos) {
    var currNum = "" + str.charAt(pos);
    var currPos = pos - 1;
    while (currPos >= 0 && pgzp().isNumber(str.charAt(currPos))) {
        currNum = str.charAt(currPos) + currNum;
        currPos--
    }
    currPos = pos + 1;
    while (currPos < str.length && pgzp().isNumber(str.charAt(currPos))) {
        currNum += str.charAt(currPos);
        currPos++
    }
    return pgzp().isNumber(currNum) ? parseInt(currNum, 10) : -1
};
PageZipper.prototype.jx = {
    http: false,
    format: "text",
    callback: function (data) {},
    error: false,
    getHTTPObject: function () {
        var http = false;
        if (typeof ActiveXObject != "undefined") {
            try {
                http = new ActiveXObject("Msxml2.XMLHTTP")
            } catch (e) {
                try {
                    http = new ActiveXObject("Microsoft.XMLHTTP")
                } catch (E) {
                    http = false
                }
            }
        } else {
            if (XMLHttpRequest) {
                try {
                    http = new XMLHttpRequest()
                } catch (e) {
                    http = false
                }
            }
        }
        return http
    },
    load: function (url, callback, format) {
        this.init();
        if (!this.http || !url) {
            return
        }
        var contentType = pgzp().getContentType();
        if (this.http.overrideMimeType && contentType) {
            this.http.overrideMimeType(contentType)
        }
        this.callback = callback;
        if (!format) {
            format = "text"
        }
        this.format = format.toLowerCase();
        var ths = this;
        this.http.open("GET", url, true);
        this.http.onreadystatechange = function () {
            if (!ths) {
                return
            }
            var http = ths.http;
            if (http.readyState == 4) {
                if (http.status == 200) {
                    var result = "";
                    if (http.responseText) {
                        result = http.responseText
                    }
                    if (ths.callback) {
                        ths.callback(result)
                    }
                } else {
                    if (ths.error) {
                        ths.error(http.status)
                    }
                }
            }
        };
        this.http.send(null)
    },
    init: function () {
        this.http = this.getHTTPObject()
    }
};
PageZipper.prototype.zero = function (n) {
    return (!pgzp().defined(n) || isNaN(n)) ? 0 : n
};
PageZipper.prototype.defined = function (o) {
    return (typeof (o) != "undefined")
};
PageZipper.prototype.css = (function () {
    var css = {};
    css.rgb2hex = function (rgbString) {
        if (typeof (rgbString) != "string" || !pgzp().defined(rgbString.match)) {
            return null
        }
        var result = rgbString.match(/^\s*rgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*/);
        if (result == null) {
            return rgbString
        }
        var rgb = +result[1] << 16 | +result[2] << 8 | +result[3];
        var hex = "";
        var digits = "0123456789abcdef";
        while (rgb != 0) {
            hex = digits.charAt(rgb & 15) + hex;
            rgb >>>= 4
        }
        while (hex.length < 6) {
            hex = "0" + hex
        }
        return "#" + hex
    };
    css.hyphen2camel = function (property) {
        if (!pgzp().defined(property) || property == null) {
            return null
        }
        if (property.indexOf("-") < 0) {
            return property
        }
        var str = "";
        var c = null;
        var l = property.length;
        for (var i = 0; i < l; i++) {
            c = property.charAt(i);
            str += (c != "-") ? c : property.charAt(++i).toUpperCase()
        }
        return str
    };
    css.getStyle = function (o, property) {
        if (o == null) {
            return null
        }
        var val = null;
        var camelProperty = css.hyphen2camel(property);
        if (property == "float") {
            val = css.getStyle(o, "cssFloat");
            if (val == null) {
                val = css.getStyle(o, "styleFloat")
            }
        } else {
            if (o.currentStyle && pgzp().defined(o.currentStyle[camelProperty])) {
                val = o.currentStyle[camelProperty]
            } else {
                if (pgzp().win.getComputedStyle) {
                    val = pgzp().win.getComputedStyle(o, null).getPropertyValue(property)
                } else {
                    if (o.style && pgzp().defined(o.style[camelProperty])) {
                        val = o.style[camelProperty]
                    }
                }
            }
        }
        if (/^\s*rgb\s*\(/.test(val)) {
            val = css.rgb2hex(val)
        }
        if (/^#/.test(val)) {
            val = val.toLowerCase()
        }
        return val
    };
    css.setStyle = function (o, property, value) {
        if (o == null || !pgzp().defined(o.style) || !pgzp().defined(property) || property == null || !pgzp().defined(value)) {
            return false
        }
        if (property == "float") {
            o.style.cssFloat = value;
            o.style.styleFloat = value
        } else {
            if (property == "opacity") {
                o.style["-moz-opacity"] = value;
                o.style["-khtml-opacity"] = value;
                o.style.opacity = value;
                if (pgzp().defined(o.style.filter)) {
                    o.style.filter = "alpha(opacity=" + value * 100 + ")"
                }
            } else {
                o.style[css.hyphen2camel(property)] = value
            }
        }
        return true
    };
    css.hasClass = function (obj, className) {
        if (!pgzp().defined(obj) || obj == null || !RegExp) {
            return false
        }
        var re = new RegExp("(^|\\s)" + className + "(\\s|$)");
        if (typeof (obj) == "string") {
            return re.test(obj)
        } else {
            if (typeof (obj) == "object" && obj.className) {
                return re.test(obj.className)
            }
        }
        return false
    };
    css.addClass = function (obj, className) {
        if (typeof (obj) != "object" || obj == null || !pgzp().defined(obj.className)) {
            return false
        }
        if (obj.className == null || obj.className == "") {
            obj.className = className;
            return true
        }
        if (pgzp().css.hasClass(obj, className)) {
            return true
        }
        obj.className = obj.className + " " + className;
        return true
    };
    css.removeClass = function (obj, className) {
        if (typeof (obj) != "object" || obj == null || !pgzp().defined(obj.className) || obj.className == null) {
            return false
        }
        if (!pgzp().css.hasClass(obj, className)) {
            return false
        }
        var re = new RegExp("(^|\\s+)" + className + "(\\s+|$)");
        obj.className = obj.className.replace(re, " ");
        return true
    };
    css.replaceClass = function (obj, className, newClassName) {
        if (typeof (obj) != "object" || obj == null || !pgzp().defined(obj.className) || obj.className == null) {
            return false
        }
        pgzp().css.removeClass(obj, className);
        pgzp().css.addClass(obj, newClassName);
        return true
    };
    return css
})();
PageZipper.prototype.screen = (function () {
    var screen = {};
    screen.getBody = function () {
        if (pgzp().doc.body) {
            return pgzp().doc.body
        }
        if (pgzp().doc.getElementsByTagName) {
            var bodies = pgzp().doc.getElementsByTagName("BODY");
            if (bodies != null && bodies.length > 0) {
                return bodies[0]
            }
        }
        return null
    };
    screen.getScrollTop = function () {
        if (pgzp().doc.documentElement && pgzp().defined(pgzp().doc.documentElement.scrollTop) && pgzp().doc.documentElement.scrollTop > 0) {
            return pgzp().doc.documentElement.scrollTop
        }
        if (pgzp().doc.body && pgzp().defined(pgzp().doc.body.scrollTop)) {
            return pgzp().doc.body.scrollTop
        }
        return null
    };
    screen.getDocumentHeight = function () {
        var body = pgzp().screen.getBody();
        var innerHeight = (pgzp().defined(self.innerHeight) && !isNaN(self.innerHeight)) ? self.innerHeight : 0;
        if (pgzp().doc.documentElement && (!pgzp().doc.compatMode || pgzp().doc.compatMode == "CSS1Compat")) {
            var topMargin = parseInt(pgzp().css.getStyle(body, "margin-top"), 10) || 0;
            var bottomMargin = parseInt(pgzp().css.getStyle(body, "margin-bottom"), 10) || 0;
            return Math.max(body.offsetHeight + topMargin + bottomMargin, pgzp().doc.documentElement.clientHeight, pgzp().doc.documentElement.scrollHeight, pgzp().zero(self.innerHeight))
        }
        return Math.max(body.scrollHeight, body.clientHeight, pgzp().zero(self.innerHeight))
    };
    screen.getViewportWidth = function () {
        if (pgzp().doc.documentElement && (!pgzp().doc.compatMode || pgzp().doc.compatMode == "CSS1Compat")) {
            return pgzp().doc.documentElement.clientWidth
        } else {
            if (pgzp().doc.compatMode && pgzp().doc.body) {
                return pgzp().doc.body.clientWidth
            }
        }
        return screen.zero(self.innerWidth)
    };
    screen.getViewportHeight = function () {
        if (!pgzp().win.opera && pgzp().doc.documentElement && (!pgzp().doc.compatMode || pgzp().doc.compatMode == "CSS1Compat")) {
            return pgzp().doc.documentElement.clientHeight
        } else {
            if (pgzp().doc.compatMode && !pgzp().win.opera && pgzp().doc.body) {
                return pgzp().doc.body.clientHeight
            }
        }
        return pgzp().zero(self.innerHeight)
    };
    return screen
})();

function pgzp() {
    return window._page_zipper_is_bookmarklet ? window.currPgzp : window.content.currPgzp
}

function _pgzpOnTabChange() {
    if (window.content._pgzpTab && window.content._pgzpTab.selected) {
        _pgzpSetButtonStatus(pgzp().is_running)
    } else {
        _pgzpSetButtonStatus(false)
    }
}

function _pgzpUnloadPgzp() {
    _pgzpSetButtonStatus(false)
}

function _pgzpSetButtonStatus(active) {
    var pgzpButton = document.getElementById("pagezipper-button");
    pgzpButton.style.listStyleImage = "url('chrome://pagezipper/skin/zipper_24" + (active ? "_green" : "") + ".png')"
}

function _pgzpInitExtension() {
    window.content.currPgzp = new PageZipper();
    pgzp().win = window.content;
    pgzp().doc = pgzp().win.document;
    pgzp().is_extension = true;
    pgzp().media_path = "chrome://pagezipper/skin/";
    pgzp().loadPageZipper();
    gBrowser.tabContainer.onselect = _pgzpOnTabChange;
    window.content._pgzpTab = gBrowser.selectedTab;
    pgzp().addEventListener(pgzp().win, "unload", _pgzpUnloadPgzp)
}

function _pgzpToggleExtension() {
    if (!window.content.currPgzp) {
        _pgzpInitExtension()
    }
    if (pgzp().is_running) {
        pgzp().stopPageZipper();
        _pgzpSetButtonStatus(false)
    } else {
        pgzp().runPageZipper();
        _pgzpSetButtonStatus(true)
    }
}

function _pgzpInitBookmarklet() {
    window.currPgzp = new PageZipper();
    pgzp().win = window;
    pgzp().doc = window.document;
    pgzp().is_bookmarklet = true;
    pgzp().media_path = "http://www.printwhatyoulike.com/static/pagezipper/ui/";
    pgzp().loadPageZipper()
}

function _pgzpToggleBookmarklet() {
    if (!pgzp().is_running) {
        pgzp().runPageZipper()
    }
}
if (window._page_zipper_is_bookmarklet) {
    _pgzpInitBookmarklet();
    _pgzpToggleBookmarklet()
}

function pgzp() {
    return window.currPgzp
}

function _pgzpInitPwyl() {
    window.currPgzp = new PageZipper();
    pgzp().win = window;
    pgzp().doc = window.document;
    if (!window.Node) {
        window.Node = {
            ELEMENT_NODE: 1,
            TEXT_NODE: 3
        }
    }
    pgzp().currDomain = pgzp().getDomain(ppw.props.pageUrl);
    pgzp().url_list = [ppw.props.pageUrl];
    var nextPage = pgzp().buildPage(pgzp().doc.body, ppw.props.pageUrl);
    pgzp().pages.push(nextPage);
    nextPage.nextLink = pgzp().getNextLink(nextPage.page);
    if (nextPage.nextLink && nextPage.nextLink.finalScore > 17000) {
        var addNextPageButton = document.getElementById("ppw_add_next_page_button");
        addNextPageButton.style.display = ""
    }
};


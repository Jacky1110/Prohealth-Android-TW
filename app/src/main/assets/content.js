// JavaScript Document

	// alert('radio:' + window.devicePixelRatio);

	var scrollTop = 0;				// 目前Y軸位移的位置
	var dateValue = '';			// 日期數值;
	var timeValue = '';			// 時間數值;
	var peopleValue = '';			// 人數
	var hostValue = '';			// 主辦單位
	var costValue = '';			// 費用說明
	var phoneValue = '';			// 聯絡電話
	
	// 這是用來通知Android當發生scroll時的相關處理;
	function scrollProcess() {
	
		scrollTop = document.body.scrollTop * window.devicePixelRatio;
		
		if(typeof(android)) {		// 傳值給上層的Activity實體;
			
				android.scrollProcess(scrollTop);	
				
			}
		
		if(document.getElementById('scrollValue')) {
		
			document.getElementById('scrollValue').innerHTML = scrollTop;
			
		}
		
	}
	
	// 呼叫上層的Link連結
	function linkToMap(mapString) {
		
		if(typeof(android)) {		// 傳值給上層的Activity實體;
			
				android.linkToMap(mapString);	
				
			}
		
	}
	
	// 呼叫上層的URL字串
	function linkToURL(urlString) {
		
		if(typeof(android)) {		// 傳值給上層的Activity實體;
			
				android.linkToMap(urlString);	
				
			}
		
	}
	
	// 呼叫上層的電話
	function linkToPhone(phoneString) {
		
		if(typeof(android)) {		// 傳值給上層的Activity實體;
			
				android.linkToMap(phoneString);	
				
			}
		
	}
	
	// 新增加內容
	function addTextToContent(contentString) {
	
		// alert(contentString);
		//document.body.innerHTML = contentString;
		
		if(document.getElementById('contentArea')) {
		
			document.getElementById('contentArea').innerHTML = contentString;
			
		}
		
	}
	
	
	var ifCompletedProcess = false;
	
	// 頁面讀取成功後，需要進行的必要處理;
	function onloadProcess() { 
	
		if(document.getElementById('fileArea')) {
		
			document.getElementById('fileArea').innerHTML ='';
						
		}
	
		if(typeof(window.android) != 'undefined' && !ifCompletedProcess) {		// 傳值給上層的Activity實體;
		
				if(typeof(window.android.getContentString) != 'undefined') {
					
					// alert('screen.availWidth:' + screen.availWidth);
					// alert('screen.availHeight:' + screen.availHeight);
					// alert('screen.availTop:' + screen.availTop);
					// alert('screen.availLeft:' + screen.availLeft);
					// alert('screen.pixelDepth:' + screen.pixelDepth);
					// alert('screen.width:' + screen.width);
					// alert('screen.height:' + screen.height);
					// alert('window.devicePixelRatio:' + window.devicePixelRatio);
					
					ifCompleteProcess = true;
					var contentString = android.getContentString();
					addTextToContent(contentString);
					
					// setBodyTopMargin((screen.availHeight / 2) / window.devicePixelRatio);
					//alert('I will set the topMargin value: ' + screen.availHeight / 2.0);
					// setBodyTopMargin((screen.availHeight / 2.0));
					
					// 相關的設定，設定一開始的起始高度

					var intMarginTop = android.getMarginTop();
					var intMarginBottom = android.getMarginBottom();

					if(intMarginBottom > -1)
					{
						setBodyBottomMargin(intMarginBottom);
					}

					if(intMarginTop > -1)
					{
						setBodyTopMargin(intMarginTop);
					}
					else
					{
						if(screen.availWidth == 720) {

                    		setBodyTopMargin((screen.availHeight / 2) / window.devicePixelRatio);

                    	} else {

                    		setBodyTopMargin((screen.availHeight / 2.1));

                    	}
					}



					setDateValue(android.getEventDateValue());
					setTimeValue(android.getEventTimeValue());
					setPlaceValue(android.getEventPlaceValue());
					setPeopleValue(android.getEventPeopleValue());
					setHostValue(android.getEventHostValue());
					setCostValue(android.getEventCostValue());
					setPhoneValue(android.getEventPhoneValue());
					android.listAllDownloadFiles();
					// alert();
					// alert('onloadProcess complete!');
					
				} else {
				
					// alert('android.getContentString is undefined, type=' + typeof(window.android.getContentString) +  '!');
				
				}
				
				
		} else {
		
			// alert('android is undefined!');
			// alert('can not get Javascript Interface!');
		
		}
		
		// setDateValue('2014-03-25');
		// setTimeValue('22:00');
		// setPlaceValue('台北市南港區三重路19-11號3F346室');
		// setPeopleValue('10000');
		// setHostValue('嵐奕科技有限公司');
		// setCostValue('每人繳交100元');
		// setPhoneValue('(02)2655-1200');
		
		// setBodyTopMargin(800);
	
	}
	
	
	
	// 以下是外部呼叫的相關處理
	
	function setDateValue(value) {
		
		dateValue = value;
		
		if(dateValue != '' && typeof(value) != 'undefined') {
		
			if(document.getElementById('fieldTime')) {
				document.getElementById('fieldTime').style.display = '';
				
			}
			
			if(document.getElementById('valueOfDate')) {
			
				document.getElementById('valueOfDate').innerHTML = dateValue;
				
				
			}
			
		} else {
		
			if(document.getElementById('fieldTime')) {
				document.getElementById('fieldTime').style.display = 'none';
				
			}	
			
		}
		
	}
	
	// 設定日期的變數
	function setTimeValue(value) {
	
		timeValue = value;
		
		if(timeValue != '' && typeof(value) != 'undefined') {
		
			if(document.getElementById('fieldTime')) {
			
				document.getElementById('fieldTime').style.display = '';
				
			}
			
			if(document.getElementById('valueOfTime')) {
			
				document.getElementById('valueOfTime').innerHTML = timeValue;
				
			}
			
		} 
	}
	
	//設定地點的變數
	function setPlaceValue(value) {
	
		placeValue = value;
		
		if(placeValue != '' && typeof(value) != 'undefined') {
		
			if(document.getElementById('fieldPlace')) {
			
				document.getElementById('fieldPlace').style.display = '';
				
			}
			
			if(document.getElementById('valueOfPlace')) {
			
				document.getElementById('valueOfPlace').innerHTML = placeValue;
				
			}
			
		} else {
		
			if(document.getElementById('fieldPlace')) {
			
				document.getElementById('fieldPlace').style.display = 'none';
				
			}
			
		}
		
	}
	
	
	//設定人數的變數
	function setPeopleValue(value) {
	
		peopleValue = value;
		
		if(peopleValue == '0') {
			peopleValue = '沒有限制';			// 傳入的值如果為0,表示沒有限制，請顯示成沒有限制！
		}
		
		
		if(peopleValue != '' && typeof(value) != 'undefined') {
		
			if(document.getElementById('fieldPeople')) {
			
				document.getElementById('fieldPeople').style.display = '';
				
			}
			
			if(document.getElementById('valueOfPeople')) {
			
				document.getElementById('valueOfPeople').innerHTML = peopleValue;
				
			}
			
		} else {
			
			if(document.getElementById('fieldPeople')) {
			
				document.getElementById('fieldPeople').style.display = 'none';
				
			}
			
		}
		
	}
	
	//設定主辦單位的變數
	function setHostValue(value) {
	
		
		hostValue = value;
		
		if(hostValue != '' && typeof(value) != 'undefined') {
		
			if(document.getElementById('fieldHost')) {
			
				document.getElementById('fieldHost').style.display = '';
				
			}
			
			if(document.getElementById('valueOfHost')) {
			
				document.getElementById('valueOfHost').innerHTML = hostValue;
				
			}
			
		} else {
		
			if(document.getElementById('fieldHost')) {
			
				document.getElementById('fieldHost').style.display = 'none';
				
			}	
			
		}
		
	}
	
	//設定成本的數值
	function setCostValue(value) {
	
		costValue = value;
		
		if(costValue != '' && typeof(value) != 'undefined') {
		
			if(document.getElementById('fieldCost')) {
			
				document.getElementById('fieldCost').style.display = '';
				
			}
			
			if(document.getElementById('valueOfCost')) {
			
				document.getElementById('valueOfCost').innerHTML = costValue;
				
			}
			
		} else {
			
			if(document.getElementById('fieldCost')) {
			
				document.getElementById('fieldCost').style.display = 'none';
				
			}
			
			
		}
		
	}
	
	//設定電話的變數
	function setPhoneValue(value) {
	
		phoneValue = value;
		
		if(phoneValue != '' && typeof(value) != 'undefined') {
		
			if(document.getElementById('fieldPhone')) {
			
				document.getElementById('fieldPhone').style.display = '';
				
			}
			
			if(document.getElementById('valueOfPhone')) {
			
				document.getElementById('valueOfPhone').innerHTML = phoneValue;
				
			}
			
		} else {
		
			if(document.getElementById('fieldPhone')) {
			
				document.getElementById('fieldPhone').style.display = 'none';
				
			}	
			
		}
		
	}
	
	
	// 以下是用在顯示近期活動的相關處理;
	// 時間的處理
	function callTimeProcess() {
		android.fieldDateProcess();
		// alert('callTimeProcess');
	}
	
	// 連結GoogleMap
	function callGoogleMapProcess() {
		android.fieldPlaceProcess();
		// alert('callGoogleMapProcess');
	}
	
	// 連結有關於人員的處理
	function callPeopleProcess() {
		android.fieldPeopleProcess();
		// alert('callPeopleProcess');
		
	}
	
	// 連結主辦單位的處理
	function callHostProcess() {
		android.fieldHostProcess();
		// alert('callHostProcess');
	}
	
	// 連結費用的處理
	function callCostProcess() {
		android.fieldCostProcess();
		// alert('callCostProcess');
		
	}
	
	// 連結聯絡電話的處理
	function callPhoneProcess() {
		 android.fieldPhoneProcess();
		 // alert('callPhoneProcess');
		
	}
	
	//addTextToContent('OK!!');
	
	function addNewFile(fileName, fileIconPath, fileUrl) {
	
		// alert('fileName=' + fileName);
		// alert('fileIconPath=' + fileIconPath);
		// alert('fileUrl=' + fileUrl);
		
		if(fileIconPath == '') {
			
			fileIconPath = 'file:///android_asset/icon_file.png'
			
		}
	
		var fileString = '<div class="fileItem" onclick="downloadFile(\'' + fileUrl + '\');">' +
        				  '<div class="icon"><img src="' + fileIconPath + '" /></div>' +
           			  '<div class="title">' + fileName + '</div>' +
           			  '<div class="clear"></div>' +
        				  '</div>';
						  
		if(document.getElementById('fileArea')) {
		
			document.getElementById('fileArea').innerHTML += fileString;
			
		}
		
	}
	
	function downloadFile(fileUrl) {
	
		android.downloadFile(fileUrl);	
		
	}
	
	function setBodyTopMargin(height) {
	
		if(document.getElementById('documentDetail')) {
		
			document.getElementById('documentDetail').style.marginTop = height + 'px';
			
		}
		
	}

	function setBodyBottomMargin(height) {

    		if(document.getElementById('documentDetail')) {

    			document.getElementById('documentDetail').style.marginBottom = height + 'px';

    		}

    	}
	
	
	$(document).ready(function(){
		onloadProcess();
		window.onscroll = scrollProcess;
	});
	
	
	 
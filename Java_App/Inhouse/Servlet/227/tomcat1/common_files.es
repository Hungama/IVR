function getLanguage()
 {
	  if(lang == 'notset')
	    return h;
      else return lang;
 }
 
 function audio(msg) 
 {
	  return "../promptFiles/common/" + msg + '-' + getLanguage() + '.wav';
 }
 
 function start_time()
 {
 	STime1 = padZeroH();
 	STime2 = padZeroM();
 	STime3 = padZeroS();
 	STime4=STime1+""+STime2+""+STime3;
 	return STime4;
 }

function padZeroH ()
{
	tempTime = new Date();		
	 Minutes = tempTime.getMinutes();               
	 Hour = tempTime.getHours();		
	 Seconds = tempTime.getSeconds();
	 tempTime = new Date();		
	 t = tempTime.getHours();		
	 	
		if(t < 10) {
		t = "0" + t;
			}
	
			return t;						
}
			 
 function padZeroM () 
 {
 	tempTime = new Date();		
	t = tempTime.getMinutes();		
 	
	if(t < 10)
	{
		t = "0" + t;
	}

	return t;						
}
			 
 function padZeroS () 
 {
 	tempTime = new Date();		
	 t = tempTime.getSeconds();		
 	
	if(t < 10) 
	{
		t = "0" + t;
	}

	return t;						
}
 function callgreetingtime() 
 {
 	t=start_time();
 	t=t.substring(0,2)
 	return t;
 	
}

function getRandom(max,min) 
 {
 var a=parseInt(max) ;
 var b=parseInt(min) ;
 
 rand_no=Math.floor(Math.random() * (a - b + 1)) + b;
 
return  rand_no;
 
 }
 
function time_duration(time) 
{
  	var tempTime=new Date();
	
	var starttime = time;
	//starttime="131010";
	
	var duration=0,d1=0,d2=0,d3=0;
	
	
	var str1= tempTime.getHours();
	str1 =String(str1)
	var ret="";
	var  len = str1.length;
	if (len >= 2)
		str1 = str1;
	else
	{
	  for( d=0; d<2-len; d++)
			ret = ret + "0";
			str1 = ret + str1;
	}	
	var str2=tempTime.getMinutes();
	str2 =String(str2)
	ret="";
	len = str2.length;
	if (len >= 2)
			str2 = str2;
	else
	{
	    for( d=0; d<2-len; d++)
			ret = ret + "0";
	  	str2 = ret + str2;
     }
     ret="";
     var str3=tempTime.getSeconds();
     str3 =String(str3)
     len = str3.length;
  	 if (len >= 2)
			str3 = str3;
	 else
	 {
	    for( d=0; d<2-len; d++)
	    	ret = ret + "0";
		str3 = ret + str3;
	 }
	
	 var   endtime=str1+str2+str3;
	 var s1,s2,m1,m2,h1,h2,diffseconds=0,diffminutes=0,diffhours=0;
	
	 if(starttime=="0")
	 {
	    duration=0;
	
	 }	
	 else
	 {	
	
		h1=parseInt(starttime.substring(0,2));
	    h2=parseInt(endtime.substring(0,2));
		m1=parseInt(starttime.substring(2,4));
		m2=parseInt(endtime.substring(2,4));
		s1=parseInt(starttime.substring(4,6));
		s2=parseInt(endtime.substring(4,6));
		if(h1>h2)
		{
		   d2=s2+60*m2+60*60*h2;
		   d1=(59-s1)+(59-m1)*60+(23-h1)*60*60;
		   duration=d1+d2;

     		//	System.out.println("Duration in Seconds \t"+duration);
		}
		else
		{
			if(s2<s1)
			{
	 				s2+=60;
	  				--m2;
	  				diffseconds=s2-s1;
	
			}
			else
			{
	 				diffseconds=s2-s1;
	
			}
			if(m2<m1)
			{
	 				m2+=60;
	  				--h2;
	  				diffminutes=m2-m1;
			}
			else
			{
	 				diffminutes=m2-m1;
			}
	   }
	   diffhours=h2-h1;
	   duration=diffseconds+(60*diffminutes)+(60*60*diffhours);
	                  //     duration=duration/60;
	                 //    d3 = Math.ceil(duration);
	   return duration;
	
    }
  	
}

function chkBalenceMinute(time) 
{
  	var tempTime=new Date();
	
	var starttime = time;
	//starttime="131010";
	
	var duration=0,d1=0,d2=0,d3=0;
	
	
	var str1= tempTime.getHours();
	str1 =String(str1)
	var ret="";
	var  len = str1.length;
	if (len >= 2)
		str1 = str1;
	else
	{
	  for( d=0; d<2-len; d++)
			ret = ret + "0";
			str1 = ret + str1;
	}	
	var str2=tempTime.getMinutes();
	str2 =String(str2)
	ret="";
	len = str2.length;
	if (len >= 2)
			str2 = str2;
	else
	{
	    for( d=0; d<2-len; d++)
			ret = ret + "0";
	  	str2 = ret + str2;
     }
     ret="";
     var str3=tempTime.getSeconds();
     str3 =String(str3)
     len = str3.length;
  	 if (len >= 2)
			str3 = str3;
	 else
	 {
	    for( d=0; d<2-len; d++)
	    	ret = ret + "0";
		str3 = ret + str3;
	 }
	
	 var   endtime=str1+str2+str3;
	 var s1,s2,m1,m2,h1,h2,diffseconds=0,diffminutes=0,diffhours=0;
	
	 if(starttime=="0")
	 {
	    duration=0;
	
	 }	
	 else
	 {	
	
		h1=parseInt(starttime.substring(0,2));
	    h2=parseInt(endtime.substring(0,2));
		m1=parseInt(starttime.substring(2,4));
		m2=parseInt(endtime.substring(2,4));
		s1=parseInt(starttime.substring(4,6));
		s2=parseInt(endtime.substring(4,6));
		if(h1>h2)
		{
		   d2=s2+60*m2+60*60*h2;
		   d1=(59-s1)+(59-m1)*60+(23-h1)*60*60;
		   duration=d1+d2;

     		//	System.out.println("Duration in Seconds \t"+duration);
		}
		else
		{
			if(s2<s1)
			{
	 				s2+=60;
	  				--m2;
	  				diffseconds=s2-s1;
	
			}
			else
			{
	 				diffseconds=s2-s1;
	
			}
			if(m2<m1)
			{
	 				m2+=60;
	  				--h2;
	  				diffminutes=m2-m1;
			}
			else
			{
	 				diffminutes=m2-m1;
			}
	   }
	   diffhours=h2-h1;
	   duration=diffseconds+(60*diffminutes)+(60*60*diffhours);
	                  //     duration=duration/60;
	                 //    d3 = Math.ceil(duration);
	   return duration;
	
    }
  	
}

function getcurrdate() 
 {
 	tempTime = new Date();		
	t = tempTime.getDate();
 	
	if(t < 10)
	{
		t = "0" + t;
	}

	return t;						
}

function getcurryyyymmdd() 
{
 	tempTime = new Date();		
	yy = tempTime.getFullYear();		
	mm = tempTime.getMonth()+1;		
	dd = tempTime.getDate();	
 	
	if(mm < 10)
	{
		mm= "0" + mm;
	}
	
	if(dd < 10)
	{
		dd = "0" + dd;
	}
    t= yy + mm + dd;
	return t;						
}

function getcontentdir(strdata) 
{

   var contentdir = strdata.split("_");
 
   var dirIs=contentdir[0];
 
   return  dirIs;
 
}

function AddContentTime(compare_time1,ContentTime1)
{
	if(isNaN(compare_time1)==false){
		ContentTime1=ContentTime1+compare_time1;
	}
	else{
		ContentTime1=ContentTime1+3;
	}
	return ContentTime1;


}
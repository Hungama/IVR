var num2play_Array;
	
function extractDigit(number,length)
{
	var retnum = "";
	var chkvar = 0;
	num2play_Array = new Array();
	var arrctr = 0;
	var lengthctr = length;
	while(lengthctr > 0)
	{
		var subnum;
		if(lengthctr == 7)
		{
			subnum = number.substr(0,2);
			num2play_Array[arrctr] = subnum; 
			num2play_Array[arrctr+1] = "Lakhs"; 
			arrctr+=2;
			lengthctr = lengthctr - 2;
			number = number.substr(2,lengthctr);
		}
		else if(lengthctr == 6)
		{
			subnum = number.substr(0,1);
			if(subnum != "0")
			{
				num2play_Array[arrctr] = subnum; 
				num2play_Array[arrctr+1] = "Lakh"; 
				arrctr+=2;
			}
			lengthctr = lengthctr - 1;
			number = number.substr(1,lengthctr);
		}
		else if(lengthctr == 5)
		{
			subnum = number.substr(0,2);
			if(subnum != "00")
			{
				num2play_Array[arrctr] = subnum; 
				<!--num2play_Array[arrctr+1] = "Thousands"; -->
				num2play_Array[arrctr+1] = "1000";
				arrctr+=2;
			}
			lengthctr = lengthctr - 2;
			number = number.substr(2,lengthctr);
		}
		else if(lengthctr == 4)
		{
			subnum = number.substr(0,1);
			if(subnum != "0")
			{
				num2play_Array[arrctr] = subnum; 
				<!--num2play_Array[arrctr+1] = "Thousand"; -->
				num2play_Array[arrctr+1] = "1000";
				arrctr+=2;
			}
			lengthctr = lengthctr - 1;
			number = number.substr(1,lengthctr);
		}
		else if(lengthctr == 3)
		{
			subnum = number.substr(0,1);
			if(subnum != "0")
			{
				num2play_Array[arrctr] = subnum; 
				<!--num2play_Array[arrctr+1] = "Hundred"; -->
				num2play_Array[arrctr+1] = "100";
				arrctr+=2;
			}
			lengthctr = lengthctr - 1;
			number = number.substr(1,lengthctr);
		}
		else if(lengthctr == 2)
		{
			subnum = number.substr(0,2);
			if(subnum != "00")
			{
				num2play_Array[arrctr] = subnum; 
				arrctr+=2;
			}
			lengthctr = lengthctr - 2;
			number = number.substr(2,lengthctr);
		}
		else if(lengthctr == 1)
		{
			subnum = number.substr(0,1);
			if(subnum != "0")
			{
				num2play_Array[arrctr] = subnum; 
				arrctr++;
			}
			lengthctr = lengthctr - 1;
		}
	}
}

function getPromptCtr(score)
{
    var num2play=score.toString();
	var numlength = num2play.length;
	var num2play_string;
	var count;
	extractDigit(num2play,numlength);
	count = num2play_Array.length;
	return(count);
}

function plyNumPrompt(icounter)
{
	return(num2play_Array[icounter]);
}


package com.jiuzhansoft.ehealthtec.sphygmomanometer;

import java.util.ArrayList;
import java.util.List;

import com.jiuzhansoft.ehealthtec.sphygmomanometer.utils.Average;

public class First implements Average {
   
	@Override
	public <Int> int getAverage(List<Int> list) {
		// TODO Auto-generated method stub
	    	Integer sum = 0;
	        for(int i=0;i<list.size();i++){
	            sum = sum + (Integer) list.get(i);
	        }
	        return (int) (sum);
	}
}

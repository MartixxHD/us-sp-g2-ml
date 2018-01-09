package intellitank.main;

import java.util.ArrayList;
import java.util.List;

import intellitank.Logger;
import intellitank.utils.PriceList;
import intellitank.utils.Timestamp;

public class Calculator
{
	public static int forecastPrice(int id, Timestamp knowPrice, Timestamp forecastTime)
	{
		int result = 0; 	
		int daysback = 7;
		
		Timestamp lastTime = knowPrice.clone();
		lastTime.setDay(lastTime.getDay() - daysback);
		lastTime.clean();
		
		knowPrice.clean();
		
		PriceList prices = PriceList.fromString(DataStorage.getStationPriceList(id));
		prices.clean(lastTime.clone(), knowPrice.clone());
		
		List<Integer> averages = new ArrayList<>();
		
		Timestamp currentTime = lastTime.clone();
		
		while(currentTime.compare(knowPrice) != 0)
		{
			for(int h=0; h<24; h++)
			{
				if(h == forecastTime.getHour())
				{
					Timestamp time = currentTime.clone();

					int temp = 0;
					
					time.setHour(time.getHour() - 1);
					temp += prices.getPriceAt(time);

					time.setHour(time.getHour());
					temp += prices.getPriceAt(time);

					time.setHour(time.getHour() + 1);
					temp += prices.getPriceAt(time);

					averages.add(temp / 3);
				}
			}
			
			currentTime.setDay(currentTime.getDay() + 1);
		}
		
		Logger.log("averages > " + averages);
		
		if(!averages.isEmpty())
		{
			for(Integer value : averages)
			{
				result += value;
			}
			
			result /= averages.size();
		}
		
		Logger.log("result > " + result);
		
		return result;
	}
}

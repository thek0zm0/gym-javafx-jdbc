package model.services;

import java.util.ArrayList;
import java.util.List;

import mode.entities.Plans;

public class PlanService 
{
	public List<Plans> findAll()
	{
		//MOCK
		List<Plans> list = new ArrayList<>();
		list.add(new Plans(1,"Treino de adaptacao"));
		list.add(new Plans(2,"Treino de Basico 1"));
		list.add(new Plans(3,"Treino de Basico 2"));
		list.add(new Plans(4,"Treino de Intermediario 1"));
		list.add(new Plans(5,"Treino de Intermediario 2"));
		list.add(new Plans(6,"Treino de Avancado 1"));
		list.add(new Plans(7,"Treino de Avancado 2"));
		
		return list;
	}
}

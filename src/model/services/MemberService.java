package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.MemberDao;
import model.entities.Member;

public class MemberService 
{
	private MemberDao dao = DaoFactory.createMemberDao();
	
	public List<Member> findAll()
	{
		return dao.findAll();
	}
	
	public void saveOrUpdate(Member obj)
	{
		if(obj.getId()==null)
		{
			dao.insert(obj);
		}
		else
		{
			dao.update(obj);
		}
	}
	
	public void remove(Member obj)
	{
		dao.deleteById(obj.getId());
	}
}

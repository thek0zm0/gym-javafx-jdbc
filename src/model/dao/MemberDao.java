package model.dao;

import java.util.List;

import model.entities.Plans;
import model.entities.Member;

public interface MemberDao {

	void insert(Member obj);
	void update(Member obj);
	void deleteById(Integer id);
	Member findById(Integer id);
	List<Member> findAll();
	List<Member> findByPlans(Plans plans);
}

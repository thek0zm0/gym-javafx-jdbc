package model.dao;

import db.DB;
import model.dao.impl.PlansDaoJDBC;
import model.dao.impl.MemberDaoJDBC;

public class DaoFactory {

	public static MemberDao createMemberDao() {
		return new MemberDaoJDBC(DB.getConnection());
	}
	
	public static PlansDao createPlansDao() {
		return new PlansDaoJDBC(DB.getConnection());
	}
}

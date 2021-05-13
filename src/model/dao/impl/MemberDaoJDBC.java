package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.MemberDao;
import model.entities.Member;
import model.entities.Plans;

public class MemberDaoJDBC implements MemberDao {

	private Connection conn;
	
	public MemberDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Member obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO Member "
					+ "(Name, Email, BirthDate, Weight, PlansId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getWeight());
			st.setInt(5, obj.getPlans().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Member obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE Member "
					+ "SET Name = ?, Email = ?, BirthDate = ?, Weight = ?, PlansId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getWeight());
			st.setInt(5, obj.getPlans().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM member WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Member findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT member.*,plan.Name as PlanName "
					+ "FROM member INNER JOIN plan "
					+ "ON member.PlanId = plan.Id "
					+ "WHERE member.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Plans dep = instantiatePlans(rs);
				Member obj = instantiateMember(rs, dep);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Member instantiateMember(ResultSet rs, Plans dep) throws SQLException {
		Member obj = new Member();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setWeight(rs.getDouble("Weight"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setPlans(dep);
		return obj;
	}

	private Plans instantiatePlans(ResultSet rs) throws SQLException {
		Plans dep = new Plans();
		dep.setId(rs.getInt("PlansId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Member> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT Member.*,Plans.Name as DepName "
					+ "FROM Member INNER JOIN Plans "
					+ "ON Member.PlansId = Plans.Id "
					+ "ORDER BY Name");
			
			rs = st.executeQuery();
			
			List<Member> list = new ArrayList<>();
			Map<Integer, Plans> map = new HashMap<>();
			
			while (rs.next()) {
				
				Plans dep = map.get(rs.getInt("PlansId"));
				
				if (dep == null) {
					dep = instantiatePlans(rs);
					map.put(rs.getInt("PlansId"), dep);
				}
				
				Member obj = instantiateMember(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Member> findByPlans(Plans Plans) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT Member.*,Plans.Name as DepName "
					+ "FROM Member INNER JOIN Plans "
					+ "ON Member.PlansId = Plans.Id "
					+ "WHERE PlansId = ? "
					+ "ORDER BY Name");
			
			st.setInt(1, Plans.getId());
			
			rs = st.executeQuery();
			
			List<Member> list = new ArrayList<>();
			Map<Integer, Plans> map = new HashMap<>();
			
			while (rs.next()) {
				
				Plans dep = map.get(rs.getInt("PlansId"));
				
				if (dep == null) {
					dep = instantiatePlans(rs);
					map.put(rs.getInt("PlansId"), dep);
				}
				
				Member obj = instantiateMember(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}

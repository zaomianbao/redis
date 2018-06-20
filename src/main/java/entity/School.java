package entity;

import java.io.Serializable;

public class School implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1466028457288827166L;
	private Long id;
	private String name;
	private Integer classNumber;
	private Integer studentsNumber;
	private Integer teacherNumber;
	private String schoolMotto;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getClassNumber() {
		return classNumber;
	}
	public void setClassNumber(Integer classNumber) {
		this.classNumber = classNumber;
	}
	public Integer getStudentsNumber() {
		return studentsNumber;
	}
	public void setStudentsNumber(Integer studentsNumber) {
		this.studentsNumber = studentsNumber;
	}
	public Integer getTeacherNumber() {
		return teacherNumber;
	}
	public void setTeacherNumber(Integer teacherNumber) {
		this.teacherNumber = teacherNumber;
	}
	public String getSchoolMotto() {
		return schoolMotto;
	}
	public void setSchoolMotto(String schoolMotto) {
		this.schoolMotto = schoolMotto;
	}
	@Override
	public String toString() {
		return "School [id=" + id + ", name=" + name + ", classNumber="
				+ classNumber + ", studentsNumber=" + studentsNumber
				+ ", teacherNumber=" + teacherNumber + ", schoolMotto="
				+ schoolMotto + "]";
	}
}

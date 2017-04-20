package ygy.test.fieldValidateTest.controller;

public class Person {

	//正则：yang开头
	private String name;
	
	//0-男，1-女，2-其他
	private String sex;
	
	//>0&<200
	private Integer age;
	
	//11位
	private String phone;
	
	private Double money;
	
	//符合日期格式
	private String birthday;
	
	private Person father;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Person getFather() {
		return father;
	}

	public void setFather(Person father) {
		this.father = father;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}
}

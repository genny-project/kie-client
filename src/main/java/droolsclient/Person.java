package droolsclient;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Person {

	private String name;
	private Address address;
	private List<String> names =  new ArrayList<String>();
	private String findName;
	
	public String getFindName() {
		return findName;
	}


	public void showFindName() {
		names.stream().forEach(System.out::println);
	}


	public List<String> getNames() {
		return names;
	}


	public void setNames(List<String> names) {
		this.names = names;
	}


	public Address getAddress() {
		return address;
	}


	public void setAddress(Address address) {
		this.address = address;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Date getDateOfBirth() {
		return dateOfBirth;
	}


	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}

	
	private Date dateOfBirth;
	private int age;
	
	
	public class Address{
		private String streetName;
		public String getStreetName() {
			return streetName;
		}
		public void setStreetName(String streetName) {
			this.streetName = streetName;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		private String city;
	}
}


//name : String
//dateOfBirth : Date
//address : Address
//age : int
//streetName : String
//city : String

public class Main {
    public static void main(String[] args) {
        
        create_database dbcreate = new create_database();
        dbcreate.DataBase();
        create_table tbcreate = new create_table();
        tbcreate.table();
        insert inserts = new insert();
        inserts.insert_value();
        
        //Mainscreen mainscreen = new Mainscreen();
        Login login = new Login();
    }
    
}
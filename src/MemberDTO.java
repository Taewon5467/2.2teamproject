
public class MemberDTO
{
    private String id;
    private String pwd;
    private String nickname;
    private String phonenum;

    public String gedid()
    {
        return id;
    }
    public void setid(String id)
    {
        this.id = id;
    }

    public String gedpwd()
    {
        return pwd;
    }
    public void setpwd(String pwd)
    {
        this.pwd = pwd;
    }
     public String gednickname()
    {
        return nickname;
    }
    public void setnickname(String nickname)
    {
        this.nickname = nickname;
    }
      public String gedphonenum()
    {
        return phonenum;
    }
    public void setphonenum(String phonenum)
    {
        this.phonenum = phonenum;
    }

    @Override
    public String toString()
    {
        return "MemberDTO [id"+id +"password="+pwd +"nickname="+nickname+"phonenumber"+phonenum+"]";
    }
}

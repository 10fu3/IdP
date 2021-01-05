package net.den3.IdP.Entity.Auth;

public enum CodeChallengeMethod {
    PLANE("plane"),S256("S256"),OTHER("other"),NONE("none");
    public final String data;
    CodeChallengeMethod(String data){
        this.data = data;
    }
    public static CodeChallengeMethod of(String category){
        for (int i = 0; i < CodeChallengeMethod.values().length; i++) {
            if(CodeChallengeMethod.values()[i].data.toLowerCase().equalsIgnoreCase(category.toLowerCase())){
                return CodeChallengeMethod.values()[i];
            }
        }
        return OTHER;
    }
}

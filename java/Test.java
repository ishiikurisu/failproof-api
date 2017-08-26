import br.eng.crisjr.failproof.tools;

public class Test {
  public static final void main(String[] args) {
    String[] stuff = tools.getLists();
    System.out.println("--- # lists");
    for (String it: stuff) {
      System.out.println(it);
    }
    System.out.println("--- # list");
    String checklist = tools.getList("apps.yml");
    System.out.println(checklist);
    System.out.println("--- # titles");
    String[] titles = tools.toTitles(stuff);
    for (String it: titles) {
      System.out.println(it);
    }
    String[] links = tools.toLinks(stuff);
    System.out.println("--- # links");
    for (String it: links) {
      System.out.println(it);
    }
    System.out.println("...");
  }
}

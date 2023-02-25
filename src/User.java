import java.util.Locale;
import java.util.Scanner;

public class User {

    private final int dia;
    private final int horario;
    private boolean swim;
    private boolean tomarSol;
    private boolean kiteSurf;
    private boolean surf;
    private String provincia;



    Scanner scanner = new Scanner(System.in);

    public User(){
        System.out.println("¿Qué dia quieres ir a la playa (hoy, mañana o pasado)?");
        dia = diaDePlaya(scanner.nextLine());
        System.out.println("¿Qué horario quieres (mañana, tarde o ambas)?");
        horario = horarioEnPlaya(scanner.nextLine());
        System.out.println("¿Quieres bañarte? S/N");
        swim = isValid(scanner.nextLine());
        System.out.println("¿Quieres tomar el sol? S/N");
        tomarSol = isValid(scanner.nextLine());
        System.out.println("¿Quieres hacer kitesurf? S/N");
        kiteSurf = isValid(scanner.nextLine());
        System.out.println("¿Quieres hacer surf? S/N");
        surf = isValid(scanner.nextLine());
        System.out.println("¿A que provincia quieres ir?");
        provincia = scanner.nextLine();
        }



    private int diaDePlaya(String response){
        String res = response.toLowerCase(Locale.ROOT).split(" ")[0]; 

        if (res.equals("hoy"))
            return 0;
        else if (res.equals("mañana"))
            return 1;
        else if(res.equals("pasado"))
            return 2;
        else{
            System.out.println("Escribe un día válido (hoy, mañana o pasado)");
            return diaDePlaya(scanner.nextLine());
        }
    }

    private int horarioEnPlaya(String response){
        String res = response.toLowerCase(Locale.ROOT).split(" ")[0];

        if(res.equals("mañana"))
            return 0;
        else if(res.equals("tarde"))
            return 1;
        else if(res.equals("ambas"))
            return 2;
        else{
            System.out.println("Escribe una horario válido (mañana, tarde o ambas)");
            return horarioEnPlaya(scanner.nextLine());
        }
    }

    private boolean isValid(String response){

        while(true){

            if (response.equals("y") || response.equals("Y") || response.equals("s") || response.equals("S"))
                return true;

            if(response.equals("N") || response.equals("n"))
                return false;

            System.out.println("Enter S para si o N para no.");
            response = scanner.nextLine();

        }

    }

    public int getDia() {
        return dia;
    }

    public int getHorario() {
        return horario;
    }
    public boolean isSwim(){
        return swim;
    }

    public boolean isKiteSurf() {
        return kiteSurf;
    }
    public boolean isTomarSol(){
        return tomarSol;
    }
    public boolean isSurf(){
        return surf;
    }
    public String getProvincia(){
        return provincia;
    }
}

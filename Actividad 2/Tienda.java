package prov;
import java.util.Scanner;

public class Tienda {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		int cantidad;
		double descuento = 0;
		final double descuentoRopa =0.15;
		final double descuentoTecno =0.10;
		final double descuentoAlimento=0.05;
		double total = 0;
		do {
			System.out.println("Ingrese la cantidad de productos a comprar");
			
			while (!scanner.hasNextInt()) {
                System.out.println("Monto inválido, intente otra vez.");
                System.out.println("Ingrese la cantidad de productos a comprar");
                scanner.next();
            }
			
			cantidad = scanner.nextInt();
			if(cantidad <= 0) {
				System.out.println("Monto inválido, intente otra vez.");
                System.out.println("Ingrese la cantidad de productos a comprar");
				cantidad = scanner.nextInt();
			}
		}while(cantidad < 1 );
		
		int[] precios = new int[cantidad] ;
		int[] tipos = new int[cantidad] ;
		String[] nombres = new String[cantidad];
		int i=0;
		scanner.nextLine();
		while(i<cantidad) {
			System.out.println("Ingrese el nombre del producto");
			
			nombres[i] = scanner.nextLine();
			
			System.out.println("Ingrese el tipo del producto");
			System.out.println("1:Ropa");
			System.out.println("2:Tecnologia");
			System.out.println("3:Alimentos");
			
			while (!scanner.hasNextInt()) {
                System.out.println("ingrese el valor numerico: .");
    			System.out.println("1:Ropa");
    			System.out.println("2:Tecnologia");
    			System.out.println("3:Alimentos");
                scanner.next();
            }
			
			tipos[i] = scanner.nextInt();
			scanner.nextLine();
			
			System.out.println("Ingrese el precio del producto");
			while (!scanner.hasNextInt()) {
                System.out.println("Monto inválido.");
                scanner.next();
            }
			precios[i] = scanner.nextInt();
			scanner.nextLine();
			
			switch(tipos[i]) {
				case 1:
					descuento += precios[i]*descuentoRopa;
					break;
				case 2:
					descuento += precios[i]*descuentoTecno;
					break;
				case 3:
					descuento += precios[i]*descuentoAlimento;
					break;
				default:
					System.out.println("eso no es un tipo de producto");
					}	
			total += precios[i];
			i++;
		}
		
		double totalSinDescuento = total;
		if(total > 500) {
			total -= total * 0.10;
		}
			
		System.out.println("total sin descuento: "+totalSinDescuento);
		System.out.println("total con descuento: "+ (total - descuento));
		System.out.println("Ahorro: "+ (totalSinDescuento -(total - descuento)));
	}
}

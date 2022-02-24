package dss.projeto.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Menu {
	/** Functional interface para handlers. */
    public interface Handler {
        void execute();
    }

    /** Functional interface para pré-condições. */
    public interface PreCondition {
        boolean validate();
    }

	private String titulo;
	private List<String> opcoes;
	private List<PreCondition> disponivel;
	private List<Handler> handlers;
	private List<String> showing;

	public Menu(String titulo, List<String> opcoes) {
		this.titulo = titulo;
		this.setOpcoes(opcoes);
		this.disponivel = new ArrayList<>();
		this.handlers = new ArrayList<>();
		this.showing = new ArrayList<>();

		this.opcoes.forEach(s -> {
            this.disponivel.add(() -> true);
            this.handlers.add(() -> System.out.println("\nATENÇÃO: Opção não implementada!"));
        });
	}

	public Menu(String titulo, String[] opcoes){
		this.titulo = titulo;
		this.opcoes = new ArrayList<>(Arrays.asList(opcoes));
		this.disponivel = new ArrayList<>();
		this.handlers = new ArrayList<>();
		this.showing = new ArrayList<>();

		this.opcoes.forEach(s -> {
            this.disponivel.add(() -> true);
            this.handlers.add(() -> System.out.println("\nATENÇÃO: Opção não implementada!"));
        });
	}

	/**
	 * Indica quais as opções do menu.
	 * @param opcoes Opções a utilizar.
	 */
	public void setOpcoes(List<String> opcoes) {
		this.opcoes = new ArrayList<>();
		this.opcoes.addAll(opcoes);
	}

	/**
     * Método que regista uma uma pré-condição numa opção do menu.
     *
     * @param i índice da opção (começa em 1)
     * @param b pré-condição a registar
     */
    public void setPreCondition(int i, PreCondition b) {
        this.disponivel.set(i - 1, b);
    }

    /**
     * Método para registar um handler numa opção do menu.
     *
     * @param i indice da opção (começa em 1)
     * @param h handlers a registar
     */
    public void setHandler(int i, Handler h) {
        this.handlers.set(i - 1, h);
    }

	/**
	 * Adiciona opção à lista de opções já existentes do menu.
	 * @param opcoes Opções a adicionar.
	 */
	public void addOpcoes(String[] opcoes) {
		this.opcoes.addAll(Arrays.asList(opcoes));
		Arrays.asList(opcoes).forEach(s -> {
            this.disponivel.add(() -> true);
            this.handlers.add(() -> System.out.println("\nATENÇÃO: Opção não implementada!"));
        });
	}

	/**
	 * Mostra o menu no terminal.
	 */
	private void show() {
		ReaderWriter.clearScreen();
		this.showing = new ArrayList<>();

		tituloMenu(titulo);
		int num = 1;

		for (String opcao : opcoes) {
			if(this.disponivel.get(num - 1).validate()){
				this.showing.add(opcao);
				opcaoMenu(showing.size(), opcao);
			}
			num++;
		}
		opcaoMenu(0, "Sair");
	}

	/**
	 * Mostra o titulo do menu no terminal.
	 * @param titulo
	 */
	public void tituloMenu(String titulo) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------| ");
        sb.append(titulo);
        sb.append(" |-----------------------\n");

		ReaderWriter.printString(sb.toString());
    }

	/**
	 * Mostra uma opção no terminal.
	 * @param numero Número da opção.
	 * @param opcao A opção a mostrar.
	 */
    public void opcaoMenu(int numero ,String opcao) {
        StringBuilder sb = new StringBuilder();
        sb.append(numero).append("  -  ");
        sb.append(opcao).append("\n");

		ReaderWriter.printString(sb.toString());
    }

	/**
	 * Corre o menu até o utilizador indicar o seu fim.
	 */
	public void run() {
		int op;
		boolean run = true;
		boolean inv = false;

		while (run) {
			show();
			if (inv) {
				ReaderWriter.printString("Opção inválida!");
			}
			op = readOpcao();

			if (op == 0) {
				run = false;
			} else if (op != -1) {
				String opcao = this.showing.get(op - 1);
				Integer idxOp = this.opcoes.indexOf(opcao);
				this.handlers.get(idxOp).execute();
				inv = false;
			} else
				inv = true;

		}
	}

	/**
	 * Corre o menu uma vez.
	 */
	public void runOnce() {
		int op;
		boolean run = true;
		boolean inv = false;

		while (run) {
			show();
			if (inv) {
				ReaderWriter.printString("Opção inválida!");
			}
			op = readOpcao();

			if (op == 0) {
				run = false;
			} else if (op != -1) {
				inv = false;
				run = false;
				String opcao = this.showing.get(op - 1);
				Integer idxOp = this.opcoes.indexOf(opcao);
				this.handlers.get(idxOp).execute();
			} else
				inv = true;

		}
	}

	/**
	 * Lê uma opção do utilizador.
	 * @return Devolve o valor da opção.
	 */
	private int readOpcao() {
		int op;
		ReaderWriter.obterOpcao();

		try {
			String line = ReaderWriter.getString();
			op = Integer.parseInt(line);
		} catch (NumberFormatException e) { // Não foi inscrito um int
			op = -1;
		}
		if (op < 0 || op > this.opcoes.size()) {
			op = -1;
		}
		return op;
	}
}
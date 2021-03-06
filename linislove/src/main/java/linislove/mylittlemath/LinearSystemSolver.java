/**
 * Luokka on tarkoitettu kvadraattisen lineaarisen yhtälöryhmän
 * ratkaisemiseen.
 */
package linislove.mylittlemath;

public class LinearSystemSolver {

    /**
     * Metodi ratkaisee kvadraattisen yhtälöryhmän.
     *
     * @param system LinearSystem -olio joka sisältää yhtälöryhmän tiedot.
     * @return Ratkaisutaulukko muodossa Rational[] sisältäen ratkaisut siten
     * että indeksissä 0 on x1:n ratkaisu, indeksissä 1 x2:n ratkaisu jne.
     * @throws java.lang.Exception jos syötteet virheelliset tai yritetty jakaa
     * nollalla.
     *
     */
    public static Rational[] solveSystem(LinearSystem system) throws Exception {
        int rows = system.getB().length;
        Rational b[] = new Rational[rows];
        Rational[][] originalB = system.getB();
        for (int i = 0; i < rows; i++) {
            b[i] = originalB[i][0];
        }
        return solve(Count.createCopy(system.getA()), b);
    }

    /**
     * Metodi ratkaisee yhtälön Ab = x Gaussin eliminointimenetelmällä
     * osittaispivotoimalla. Huomioitava, että metodi muokkaa parametreina
     * annettuja taulukoita.
     *
     * @param originalA Rational[][] taulukko matriisista A
     * @param originalB Rational[] taulukko matriisista b
     * @return Rational[] taulukko jossa yhtälöryhmän vastaukset.
     *
     */
    private static Rational[] solve(Rational originalA[][], Rational originalB[]) throws Exception {

        int n = originalB.length;
        int[] index = new int[n];
        Rational x[] = new Rational[n];
        Rational a[][] = originalA;
        Rational b[] = originalB;
        // Muuttaa matriisin yläkolmiomatriisiksi
        gaussian(a, index);

        // Päivitetään b
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                b[index[j]] = Count.difference(b[index[j]], Count.product(a[index[j]][i], b[index[i]]));
            }
        }
        // Takaisinsijoitus
        Rational numberInDiagonal = a[index[n - 1]][n - 1];

        try {
            x[n - 1] = b[index[n - 1]].divide(numberInDiagonal);
        } catch (Exception e) {
            throw new IllegalArgumentException("Yhtälöryhmällä ei ole ratkaisua "
                    + "tai sillä ei ole yksikäsitteistä ratkaisua.");
        }
        for (int i = n - 2; i >= 0; --i) {
            x[i] = b[index[i]];
            for (int j = i + 1; j < n; ++j) {
                x[i] = x[i].minus(a[index[i]][j].multiply(x[j]));
            }
            numberInDiagonal = a[index[i]][i];
            try {
                x[i] = x[i].divide(numberInDiagonal);
            } catch (Exception e) {
                throw new IllegalArgumentException("Yhtälöryhmällä ei ole ratkaisua "
                        + "tai sillä ei ole yksikäsitteistä ratkaisua.");
                /*
                if (x[i].isZero() && numberInDiagonal.isZero()) 
                    throw new IllegalArgumentException("Yhtälöryhmälle ei ole olemassa "
                        + "yksikäsitteistä ratkaisua.");
                else throw new IllegalArgumentException("Yhtälöryhmällä ei ole ratkaisua.");
                 */
            }
        }
        return x;
    }

    /**
     * Metodi toteuttaa Gaussin eliminointimenetelmällä yläkolmiomatriisin.
     *
     * @param a Matriisi (Rational[][] a) joka muunnetaan.
     * @param index pivotointijärjestys tallennetaan tähän.
     */
    public static void gaussian(Rational a[][], int index[]) {
        //Rational detOfRowOps = Rational.ONE;
        int n = index.length;
        Rational c[] = new Rational[n];

        // Initialisoidaan index
        for (int i = 0; i < n; ++i) {
            index[i] = i;
        }

        // Etsitään skaalautuvat tekijät, yksi per rivi
        for (int i = 0; i < n; ++i) {
            Rational c1 = Rational.ZERO;
            for (int j = 0; j < n; ++j) {
                Rational c0 = Count.abs(a[i][j]);
                if (c0.greaterThan(c1)) {
                    c1 = c0;
                }
            }
            c[i] = c1;
        }

        // Etsitään kunkin sarakkeen pivotoiva alkio
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            Rational pi1 = Rational.ZERO;
            for (int i = j; i < n; ++i) {
                Rational pi0 = a[index[i]][j].abs();

                // Rivioperaatiomatriisin determinantin seuraaminen
                pi0 = pi0.divide(c[index[i]]);
                if (pi0.greaterThan(pi1)) {
                    pi1 = pi0;
                    k = i;
                }
            }

            //Järjestä rivit pivotointijärjestyksen mukaan
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                Rational pj = a[index[i]][j].divide(a[index[j]][j]);

//                Rational pj = Count.divide(a[index[i]][j], a[index[j]][j]);
                // Sijoitetaan pivotointisuhteet diagonaalin alle
                a[index[i]][j] = pj;

                // Säädetään muut alkiot 
                for (int l = j + 1; l < n; ++l) {
                    a[index[i]][l] = Count.difference(a[index[i]][l], Count.product(pj, a[index[j]][l]));
                }
            }

        }
    }
}

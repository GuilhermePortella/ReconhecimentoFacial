package reconhecimento;

import java.awt.event.KeyEvent;
import java.util.Scanner;
import org.bytedeco.javacpp.helper.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 *
 * @author Guilherme
 */

public class Captura {

    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {

        KeyEvent tecla = null; //Verifica enventos do teclado, Para apertar uma tecla e tirar uma foto

        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat(); //Converte a imagem tirada da webCam para o tipo .Mat
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);//Obj responsavel pela captura das imagens da webcam - o 0 = webcam notebook
        camera.start();//Inicia a webcam
        
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");
        //associando detectorFace ao arquivo com algoritmo de detecção

        CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());//Tela que aparece a janela com a camera
                                                        //getDefaultGamma() significa valor de variavel 2.2
                                                        //getGamma()        significa valor de variavel 2.2
                                                        //Procedimento recomendado na documentação resulta numero 1

        Frame frameCapturado = null;//Variavel do tipo Frame, sera onde vou guardar as imagens da webCam
        Mat imagemColorida = new Mat();//Pegar oque esta em frameCapturado e atribuir a imagemColorida, a partir daqui sera possivel detectar as faces
        
        
        int numeroAmostras = 25;//Numero de fotos, indicado na documentação OpenCV
        int amostra = 1;//Contador cada foto se acrescenta uma ate chegar em 25
        System.out.println("Digite o seu ID: ");
        Scanner cadastro = new Scanner(System.in);
        int idPessoa = cadastro.nextInt();
        
                                 //Grab pegua oque esta na webCam
        while ((frameCapturado = camera.grab()) != null) {//(Pega oque esta na WebCam e joga em frameCapturado) != diferente
            
            imagemColorida = converteMat.convert(frameCapturado);//Pegando Frame e atribuindo a imagemColorida
            
            Mat imagemCinza = new Mat();//Algoritmo trabalha melhor com imagens em escala preto e branco

            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);// converter a imagem colorida para uma imagem em escala cinza
            
            RectVector facesDetectadas = new RectVector();//RectVector armazena todas as faces que ele detecta
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150), new Size(500));
                                    /*Pega a img cinza armazena em facesDetectadas - 1.1=Tamanho escala da img - 1 numero de vizinhos - 0 para versoes antigas 
                                        Size tamanho minimo Size tamanho maximo
                                    */
                                    
                                    
            if (tecla == null) {
                tecla = cFrame.waitKey(5);
                
            }
                        
            for (int i = 0; i < facesDetectadas.size(); i++) {
                Rect dadosFaces = facesDetectadas.get(0);
                rectangle(imagemColorida, dadosFaces,new Scalar(0,0,255, 0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFaces);
                resize(faceCapturada, faceCapturada, new Size(160,160));//tamanho padrão para a imagem
                if (tecla == null) {
                    tecla = cFrame.waitKey(5);
                }
                if(tecla != null){
                    if (tecla.getKeyChar() == 'q') {
                        if (amostra<= numeroAmostras) {
                            imwrite("src\\fotos\\pessoas." + idPessoa + "." + amostra + ".jpg", faceCapturada);
                            System.out.println("Foto " + amostra + " capturada\n");
                            amostra++;
                        }
                    }
                    tecla = null;
                }
            }
            if (tecla == null) {
                tecla = cFrame.waitKey(20);
            }
            if (cFrame.isVisible()) {                //Se estiver visivel (Camera)
                cFrame.showImage(frameCapturado);   //Pegar imagem na webCam
            }
            if (amostra > numeroAmostras) {
                break;
            }
        }
        cFrame.dispose();//liberar memoria da janela
        camera.stop();//para a captura
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    public static void main(String[] args) throws FrameGrabber.Exception {

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
        
        
                                 //Grab pegua oque esta na webCam
        while ((frameCapturado = camera.grab()) != null) {//(Pega oque esta na WebCam e joga em frameCapturado) != diferente
            
            imagemColorida = converteMat.convert(frameCapturado);//Pegando Frame e atribuindo a imagemColorida
            
            Mat imagemCinza = new Mat();//Algoritmo trabalha melhor com imagens em escala preto e branco
            
            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);// converter a imagem colorida para uma imagem em escala cinza
            
            RectVector facesDetectadas = new RectVector();//RectVector armazena todas as faces que ele detecta
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150,150), new Size(500,500));
           //
                                    /*Pega a img cinza armazena em facesDetectadas - 1.1=Tamanho escala da img - 1 numero de vizinhos - 0 para versoes antigas 
                                        Size tamanho minimo Size tamanho maximo
                                    */
                                    
            for (int i = 0; i < facesDetectadas.size(); i++) {
                Rect dadosFaces = facesDetectadas.get(0);
                rectangle(imagemColorida, dadosFaces,new Scalar(0,0,255, 0));
                
            }
            if (cFrame.isVisible()) {               //Se estiver visivel (Camera)
                cFrame.showImage(frameCapturado);   //Pegar imagem na webCam
            }
        }
        cFrame.dispose();//liberar memoria da janela
        camera.stop();//para a captura
    }
}

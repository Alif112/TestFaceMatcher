# TestFaceMatcher

<h3> Face Matcher Flask Server </h3>

1. First copy the **face_py_lib.tar.xz** [(git link)](http://git.iptelephony.revesoft.com/al_jamil_suvo_revesoft/facematch/-/blob/master/face_py_lib.tar.xz) in server
2. Extract and then copy this folder to `/usr/local/jakarta-tomcat-7.0.61/webapps/btcl/WEB-INF/ext_lib/face_py_lib
3. `cd /usr/local/jakarta-tomcat-7.0.61/webapps/btcl/WEB-INF/ext_lib/face_py_lib`
4. make sure in this directory it contains 
            
    *  [face_compare_flask.py](http://git.iptelephony.revesoft.com/al_jamil_suvo_revesoft/facematch/-/blob/master/face_py_lib/face_compare_flask.py)
               
    *   [facematcher.service](http://git.iptelephony.revesoft.com/al_jamil_suvo_revesoft/facematch/-/blob/master/face_py_lib/facematcher.service)
               
    *   [dlib_face_recognition_resnet_model_v1.dat](http://git.iptelephony.revesoft.com/al_jamil_suvo_revesoft/facematch/-/blob/master/face_py_lib/dlib_face_recognition_resnet_model_v1.dat)
               
    *   [shape_predictor_68_face_landmarks.dat](http://git.iptelephony.revesoft.com/al_jamil_suvo_revesoft/facematch/-/blob/master/face_py_lib/shape_predictor_68_face_landmarks.dat)
   
<!--5. Setup necessary  libraries using **library_setup.sh** [(git link)](http://git.iptelephony.revesoft.com/al_jamil_suvo_revesoft/facematch/-/blob/master/face_py_lib/library_setup.sh)-->
<!--6. Setup face matching service using **service_setup.sh** [(git link)](http://git.iptelephony.revesoft.com/al_jamil_suvo_revesoft/facematch/-/blob/master/face_py_lib/service_setup.sh)-->


<h5>1. Setup Library</h5>
All the dependency requirements are listed in library_setup.sh file. You can just run the script using 

```bash
sh library_setup.sh
```

This file is mainly suitable for Centos. But it may shows some dependency incompatibility. <br>
**If there is no error then go to [Start Service](#mark-down-id-for-start-service)
<br>Or<br>
Install the following packages if required**


<h6>For Centos:</h6>
Library: epel-release, python36, python36-setuptools, python36-devel, gcc, gcc-c++, cmake, dlib==19.10.0, numpy, imageio, scikit-learn, flask, pytesseract, scikit-build, cmake, opencv-python, scikit-image

```bash
sudo yum install -y epel-release
sudo yum install -y python36
sudo yum install -y python36-setuptools
sudo yum install -y python36-devel
sudo yum install -y gcc gcc-c++ cmake
pip3 install dlib==19.10.0
pip3 install numpy
pip3 install imageio
pip3 install scikit-learn
pip3 install flask
yum install pytesseract
pip3 install scikit-build
pip3 install cmake
pip3 install opencv-python
pip install scikit-image
```

<h6>For Ubuntu:</h6>
Library: python3, python3-setuptools, python3-dev, gcc, g++, cmake, dlib, numpy, imageio, scikit-learn, flask, pytesseract, scikit-build, cmake, opencv-python, scikit-image.

```bash
sudo apt-get install python3
sudo apt-get install -y python3-setuptools
sudo apt-get install -y python3-dev
sudo apt-get install gcc g++ cmake
pip3 install dlib
pip3 install numpy
pip3 install imageio
pip3 install scikit-learn
pip3 install flask
pip3 install pytesseract
pip3 install scikit-build
pip3 install cmake
pip3 install opencv-python
pip install scikit-image
```
If all the libraries are installed properly you can go to the next stage. 

<h5 id="mark-down-id-for-start-service">2. Start Service</h5>

Now you have to run service\_setup.sh file , it will start the facematcher.service . First it copies the file into “/etc/systemd/system”, then starts the facematcher service and enables facematcher service.

```bash
cp facematcher.service /etc/systemd/system
systemctl daemon-reload
systemctl start facematcher.service
systemctl enable facematcher.service
```

<h5>3. Test Service</h5>

Go to the directory where is copied all the files.
cd /usr/local/jakarta-tomcat-7.0.61/webapps/btcl/WEB-INF/ext_lib/face_py_lib”
Run to check the status

```bash
systemctl status facematcher.service
```

**If server doesn’t run properly, In the status you will see output regarding any library error. If you see any error, install the library and run the service_setup.sh file again and check the status.
(Carefully check if the port is available and server(tomcat) running or not)**


To check if nid image and selfie are matched call api **`http://127.0.0.1:1024/face_match/<nid_image>/<selfie_image>`** . If any path contanis ***`/`*** replace it by ***`+`***
Let for any user image from nid path is <br>`/usr/local/jakarta-tomcat-7.0.61/webapps/btcl/nidSelfieImages/123456789.jpg` <br>and that user selfie path is <br>`usr/local/jakarta-tomcat-7.0.61/webapps/btcl/selfie_picture/8809696.jpg` then selfei matching api url will be `http://127.0.0.1:1024/face_match/+usr+local+jakarta-tomcat-7.0.61+webapps+btcl+nidSelfieImages+123456789.jpg/+usr+local+jakarta-tomcat-7.0.61+webapps+btcl+selfie_picture+8809696.jpg`

The API will response ***`<matched>,<difference>`***, here **<matched>** will be either 1,0 and the **<difference>** will a floating value ranging 0 to 1. Lets API response 1,.4 that's meant its a match and difference between images is .4, again if API response 0,.65 that means those images are not matched and difference is .65


To get nid text from nid ocr api **`http://127.0.0.1:1024/nid_ocr/<nid_image>`**. If any path contanis ***`/`*** replace it by ***`+`***. It response a JSON
if api ables to read status will **200**
`{"status": 200, "nid" : "<nid number>","dob": "<date of birth>"}`
if api fails to read status wll be **400**
`{"status":400}`

<h6>Extra Configuration help</h6>

By default, this API will run in **port 1024**, it you need to change the port by editing [face_compare_flask.py](http://git.iptelephony.revesoft.com/al_jamil_suvo_revesoft/facematch/-/blob/master/face_py_lib/face_compare_flask.py) 
at the very bottom of the file. After changing the file you need to restart facematcher service 

```bash
systemctl stop facematcher.service
systemctl start facematcher.service
```
If you want to deploy in any other directory that that directory. Copy all the files to that directory and change the path declared in the facematcher.service file.

```bash
ExecStart=/usr/bin/python3 /usr/local/jakarta-tomcat-7.0.61/webapps/btcl/WEB-INF/new_lib/face_py_lib/face_compare_flask.py
```




# Mosaic-Generator

This Java program will take an input image and generate a Mosaic using tile images provided in jpg.zip. The zip file will need to be extracted before use. 

## How it Works
1. **Reading Tiles:** The program reads the set of tile images from the "sources/jpg" directory and calculates their average rgb values for placement in the Mosaic.
2. **Reading Base Image:** The base image is read from the "source/base/base.jpg" file. This will be the file you provide that you wish to turn into a Mosaic.
3. **Matching Tiles:** Tiles are matched to sections of the base image to create the Mosaic image. The program will calculate the average rgb value for each tile of the base image and match it to the appropriate tile from the "sources/jpg" directory.
4. **Creating Output Image:** The output Mosaic image is written to the "source/output" directory as "output.jpg"

## Configuration

- **Tile Images:** Extract the jpg.zip folder in the "source/jpg" directory. If you would like to use your own images for the tiles, you can ignore this step and simply place the images you wish to use as tiles here.
- **Base Image:** Provide the base image you wish to turn into a Mosaic in the "source/base" directory with the file name "base.jpg".
- **Output Image:** The output image will be written to the "source/output" directory as "output.jpg".

## Dependencies

- Java Runtime Environment (JRE)
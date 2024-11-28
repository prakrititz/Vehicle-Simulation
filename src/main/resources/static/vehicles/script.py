import os
import shutil

def process_vehicle_images(base_path):
    # Walk through all vehicle directories
    for vehicle_dir in os.listdir(base_path):
        vehicle_path = os.path.join(base_path, vehicle_dir)
        
        # Check if it's a directory
        if not os.path.isdir(vehicle_path):
            continue
        
        # Look for SEPARATE subdirectory across all subdirectories
        separate_path = None
        for subdir in os.listdir(vehicle_path):
            potential_separate_path = os.path.join(vehicle_path, subdir, 'SEPARATED')
            if os.path.isdir(potential_separate_path):
                separate_path = potential_separate_path
                break
        
        # If no SEPARATE directory found, skip this vehicle
        if not separate_path:
            print(f"No SEPARATE directory found for {vehicle_dir}")
            continue
        
        # Get PNG files
        png_files = [f for f in os.listdir(separate_path) if f.lower().endswith('.png')]
        
        # Ensure we have at least 48 images
        if len(png_files) < 48:
            print(f"Not enough images in {separate_path}")
            continue
        
        # Sort files to ensure consistent order
        png_files.sort()
        
        # Define the indices and corresponding new names
        rename_indices = [0, 11, 23, 47]  # 1st, 12th, 24th, 48th (0-based indexing)
        new_names = ['right.png', 'down.png', 'left.png', 'up.png']
        
        # Keep track of renamed files
        renamed_files = set()
        
        # Rename selected images
        for idx, new_name in zip(rename_indices, new_names):
            old_path = os.path.join(separate_path, png_files[idx])
            new_path = os.path.join(separate_path, new_name)
            
            # Rename the file
            shutil.move(old_path, new_path)
            renamed_files.add(png_files[idx])
        
        # Remove other images
        for png in png_files:
            if png not in renamed_files and png not in new_names:
                file_path = os.path.join(separate_path, png)
                if os.path.exists(file_path):
                    os.remove(file_path)
        
        print(f"Processed images in {separate_path}")

if __name__ == "__main__":
    base_path = "./"
    process_vehicle_images(base_path)

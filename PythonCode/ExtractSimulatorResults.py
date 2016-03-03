# -*- coding: utf-8 -*-
#
#
#
# For this to function correctly experiment result files should be named via 
# the convention <Protocol>_CW<cwSize>_FLOW<FlowClassSeperatedByS>_RandPHASE<0 or 1 for random phase>_Queue<SizeOFQueue>_RUN<Simulation#>.csv
#
# Additionally, all files should have the same settings, the only difference
# being the simulation number
#
#
#

__author__ = 'Ryan Brummet'
import os;
import sys;
import getopt;
import numpy;
from multiprocessing import Process, Manager;
import multiprocessing
import csv;
import datetime

# global index variable, do not modify
TIME = 0;
NODE = 1;
NODE_BACKOFF = 2;
NODE_CW = 3;
NODE_QUEUE_SIZE = 4;
NODE_TRANSMIT_ACTION = 5;
NODE_RECEIVE_ACTION = 6;
NODE_STATE = 7;
PACKET_PERIOD = 8;
PACKET_PHASE = 9;
PACKET_DEADLINE = 10;
PACKET_SLACK = 11;
PACKET_TYPE_INSTANCE_NUM = 12;
PACKET_NEXT_DEST = 13;
PACKET_CREATION_NODE = 14;
PACKET_FINAL_DEST_NODE = 15;
PACKET_SLOTS_TO_SEND_TO_NEXT_DEST = 16;
PACKET_TIME_IN_CURRENT_QUEUE = 17;
PACKET_DROPPED = 18;
PACKET_POSITION_IN_QUEUE = 19;

TEMPFILENAME = "ExperiementStatsForTime"
BASE_COLS = 21;


def getFileNames(dirPath,fileType):
    files = [];
    for fileName in os.listdir(dirPath):
        if fileName.endswith(fileType):    
            files.append(os.path.join(dirPath,fileName));
    return files;
    
    
def stdIn(argv):
    folderLocation = "";
    outputLocation = "";
    try:
        opts, args = getopt.getopt(argv, "i:o:h",["intput=", "output=","help"]);
        if not opts:
            print 'No options supplied';
            print "Usage: ExtractSimulatorResults.py -i <folderLocation> -o <outLocation>";
            sys.exit(2);
    except getopt.GetoptError:
        print "Invalid Args: ExtractSimulatorResults.py -i <folderLocation> -o <outLocation>";
        sys.exit(2);
    for opt, arg in opts:
        if opt in ("-h","--help"):
            print "Usage: ExtractSimulatorResults.py -i <folderLocation> -o <outLocation>";
            sys.exit(0);
        elif opt in ("-i","--input"):
            folderLocation = arg;
        elif opt in ("-o","--output"):
            outputLocation = arg;
        else:
            print "Unhandled Exception";
            sys.exit(1);
    return getFileNames(folderLocation, "csv"), outputLocation;
    
def parallelLoad(file, dictionary) :
    fileData = numpy.loadtxt(file, delimiter=',',skiprows=1, 
                                 usecols = [TIME,NODE,NODE_BACKOFF,NODE_CW,NODE_QUEUE_SIZE,
                                            NODE_TRANSMIT_ACTION,NODE_RECEIVE_ACTION,
                                            NODE_STATE,PACKET_POSITION_IN_QUEUE]);
    dictionary[file] = fileData;
    
    
def parallelStats(timeData, numSims, numUniqueNodes, colNames, time) :
    # Rows -> NodeID (there is a one col for this)
    # Cols -> For Each Time Unit -> NodeID, minBackOff, MaxBackOff, avgBackOff,
    #         stdBackOff, minCW, maxCW, avgCW, stdCW, minQueueSize, 
    #         maxQueueSize, avgQueueSize, stdQueueSize, QueueSize%'s,
    #         TransmiissionIdle%, TransmissionContention%, 
    #         TransmissionSuccess%, TransmissionPacketDrop%,
    #         TransmissionFailure%, ReceptionIdle%, ReceptionPacketDrop%,
    #         ReceptionSuccess%, NodeStateInActive%, NodeStateContending%
    #         

    timeDateNumpy = [];
    for fileNum in range(0,len(timeData)) :
        fileTimeDateNumpy = numpy.vstack(timeData[fileNum][:]);
        timeDateNumpy.append(fileTimeDateNumpy);
    timeDateNumpy = numpy.vstack(timeDateNumpy[:]);
    timeStats = numpy.zeros((numUniqueNodes, len(colNames)));
    for node in range(0, numUniqueNodes) :
        timeDateNodeNumpy = timeDateNumpy[timeDateNumpy[:,0] == node,:];
        timeDateNodeNumpy = timeDateNodeNumpy[timeDateNodeNumpy[:,7] <= 0,:]; # we only want the head of te nodes queue or the node itself if its queue is empty
        timeStats[node,0] = node;
        
        timeStats[node,1] = numpy.min(timeDateNodeNumpy[:,1]);
        timeStats[node,2] = numpy.max(timeDateNodeNumpy[:,1]);
        timeStats[node,3] = numpy.mean(timeDateNodeNumpy[:,1]);
        timeStats[node,4] = numpy.std(timeDateNodeNumpy[:,1]);
        timeStats[node,5] = numpy.min(timeDateNodeNumpy[:,2]);
        timeStats[node,6] = numpy.max(timeDateNodeNumpy[:,2]);
        timeStats[node,7] = numpy.mean(timeDateNodeNumpy[:,2]);
        timeStats[node,8] = numpy.std(timeDateNodeNumpy[:,2]);
        timeStats[node,9] = numpy.min(timeDateNodeNumpy[:,3]);
        timeStats[node,10] = numpy.max(timeDateNodeNumpy[:,3]);
        timeStats[node,11] = numpy.mean(timeDateNodeNumpy[:,3]);
        timeStats[node,12] = numpy.std(timeDateNodeNumpy[:,3]);
        index = 13
        for x in range(1,len(colNames) - 22) :
            timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,3] == x,:].shape[0]) / numSims;
            index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,4] == 0,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,4] == 1,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,4] == 2,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,4] == 3,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,4] == 4,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,5] == 0,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,5] == 1,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,5] == 2,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,6] == 0,:].shape[0]) / numSims;
        index = index + 1;
        timeStats[node,index] = float(timeDateNodeNumpy[timeDateNodeNumpy[:,6] == 1,:].shape[0]) / numSims;
        index = index + 1;
       
    thisColNames = colNames[:];
    for title in range(0,len(colNames)) :
        thisColNames[title] = colNames[title] + str(time);
    numpy.savetxt(TEMPFILENAME + str(time), timeStats, delimiter=',', comments = '', header =  ",".join(thisColNames));
    
        
def calculateNodeQueueStatus(files, outputLocation) :
    
    # gets size of queue
    temp = files[0].split("/");
    temp = temp[-1].split("Queue");
    temp = temp[1].split("_RUN");
    queueSize = int(temp[0]);
    
    colNames = ["NodeID", "minBackOff", "maxBackOff", "avgBackOff", 
                "stdBackOff", "minCW", "maxCW", "avgCW", "stdCW", 
                "minQueueSize", "maxQueueSize", "avgQueueSize", "stdQueueSize"];
    for i in range(0,queueSize) :
        colNames.append("QueueSize" + str(i) + "%");
    colNames.append("TransmissionIdle%");
    colNames.append("TransmissionContention%");
    colNames.append("TransmissionSuccess%");
    colNames.append("TransmissionPacketDrop%");
    colNames.append("TransmissionFailure%");
    colNames.append("ReceptionIdle%");
    colNames.append("ReceptionPacketDrop%");
    colNames.append("ReceptionSuccess%");
    colNames.append("NodeStateInActive%");
    colNames.append("NodeStateContending%");
    
    # gets the num of simulations
    numSims = len(files);
    
    # gets the amount of time each simulation was run
    data = numpy.loadtxt(files[0], delimiter=',',skiprows=1);
    maxTime = int(data[-1,0]) + 1;
    
    # gets the id of each unique node.  This may not get every node in the
    # topology since this only exacts nodes that we have info for.  Nodes that
    # never receive of send packets will not have any information for them
    numUniqueNodes = [];
    nodeReferenceIndexes = [NODE, PACKET_CREATION_NODE,PACKET_NEXT_DEST, PACKET_FINAL_DEST_NODE];
    for i in range(0, maxTime) :
        line = data[i,:];
        nodeReferences = [line[x] for x in nodeReferenceIndexes]
        numUniqueNodes[:] = numUniqueNodes + list(set(nodeReferences) - set(numUniqueNodes));
    numUniqueNodes = len([item for item in numUniqueNodes if item >= 0]);
    
    print "-Loading Data";
    print datetime.datetime.time(datetime.datetime.now());
    print "";
    maxNumProcesses = multiprocessing.cpu_count();
    manager = Manager();
    dictionary = manager.dict();
    processList = [];
    for file in files :
        if len(processList) < maxNumProcesses :
            p = Process(target=parallelLoad, args=(file, dictionary));
            p.start();
            processList.append(p);
        else :
            processList[0].join();
            for i in range(1,maxNumProcesses) :
                processList[i - 1] = processList[i];
            processList.pop(-1);            
            p = Process(target=parallelLoad, args=(file, dictionary));
            p.start();
            processList.append(p);
    for process in processList :
        process.join();
    loadedData = dictionary.values();            
    dictionary = None;
    
    print "-Calculating Statistics";
    print datetime.datetime.time(datetime.datetime.now());
    print "";
    indexes = numpy.zeros((1,len(loadedData)));
    processList = [];
    numCols = loadedData[0][0,:].shape[0];
    for time in range(0,maxTime) :
        fileIndex = 0;
        specificTimeData = [];
        for file in loadedData :
            specificFileTimeData = [];
            while indexes[0,fileIndex] < file.shape[0] and file[indexes[0,fileIndex],TIME] == time:
                specificFileTimeData.append(file[indexes[0,fileIndex],range(1,numCols)]);
                indexes[0,fileIndex] = indexes[0,fileIndex] + 1;
            fileIndex = fileIndex + 1;
            specificTimeData.append(specificFileTimeData);
        if len(processList) <= maxNumProcesses :
            p = Process(target=parallelStats, args=(specificTimeData, numSims, numUniqueNodes, colNames, time));
            p.start();
            processList.append(p);
        else :
            processList[0].join();
            for i in range(1,maxNumProcesses) :
                processList[i - 1] = processList[i];
            processList.pop(-1);
            p = Process(target=parallelStats, args=(specificTimeData, numSims, numUniqueNodes, colNames, time));
            p.start();
            processList.append(p);
    loadedData = None;
    for process in processList :
        process.join();
#    
    print "-Aggregating Results";
    print datetime.datetime.time(datetime.datetime.now());
    print "";
    stats = [];
    for time in range(0,maxTime) :
        cr = csv.reader(open(TEMPFILENAME + str(time),"rb"));
        tempFile = [];
        for row in cr:
            tempFile.append(row);
        os.remove(TEMPFILENAME + str(time));
        if time == 0 :
            tempFile[0][0] = "NodeID";
            for node in range(0,numUniqueNodes + 1) :
                stats.append(tempFile[node]);
        else :
            for node in range(0,numUniqueNodes + 1) :
                row = tempFile[node];
                row.pop(0);
                stats[node] = stats[node] + row;
    
    print "-Writing Results To File";
    print datetime.datetime.time(datetime.datetime.now());
    print "";
    f = open(outputLocation, 'wb');
    for line in range(0,numUniqueNodes + 1) :
        print >> f, ",".join(stats[line]);
    f.close();
               
        
def main(args) :
    print "\n\nThis Can Take Awhile If The Number Of Simulations Or Time Slots Was High";
    print "----- Temporary Files Will Be Created While Running.  DO NOT DELETE THEM. They Will Be Deleted Automatically On Exiting -----";
    print "----- RUNNING MULTIPLE INSTANCES OF THIS SCRIPT AT THE SAME TIME WILL CAUSE DATA TO BECOME CORRUPTED";
    print datetime.datetime.time(datetime.datetime.now());
    print "";
    files, outputLocation = stdIn(args);
    calculateNodeQueueStatus(files, outputLocation);
    
    print datetime.datetime.time(datetime.datetime.now());
    print "Exiting...\n\n";


if __name__ == '__main__':
    main(sys.argv[1:])
    
    